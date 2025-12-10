package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.CreatureTypeRegistry;
import ua.hudyma.domain.creatures.dto.CreatureSkillValue;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.dto.ModifiableData;
import ua.hudyma.domain.creatures.dto.SplitReqDto;
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.CreatureSlotRespDto;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.exception.ArmyFreeSlotOverflowException;
import ua.hudyma.exception.EnumMappingErrorException;
import ua.hudyma.exception.MinimalUnitOperationException;
import ua.hudyma.mapper.ArmyMapper;
import ua.hudyma.mapper.EnumMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class ArmyService {
    private final HeroService heroService;
    private final ArmyMapper armyMapper;
    private final CreatureService creatureService;
    private final Integer ARMY_SLOT_MAX_QTY = 7;

    @Transactional
    public String compressArmy(String heroId) {
        var army = heroService.getHero(heroId).getArmyList();
        Map<CreatureType, CreatureSlot> merged = new LinkedHashMap<>();
        for (CreatureSlot slot : army) {
            merged.compute(slot.getType(), (type, existingSlot) -> {
                if (existingSlot == null) {
                    return slot;
                } else {
                    existingSlot.setQuantity(existingSlot.getQuantity() + slot.getQuantity());
                    log.info(" :::: Slot {} of {} merged with slot {}", existingSlot.getSlotId(),
                            existingSlot.getType(), slot.getSlotId());
                    return existingSlot;
                }
            });
        }
        int iterations = army.size() - merged.size();
        army.clear();
        army.addAll(merged.values());
        return " :::: Army successfully compressed in " + iterations + " merge operations";
    }

    @Transactional
    public String deleteArmy(String heroId) {
        var hero = heroService.getHero(heroId);
        var armyList = hero.getArmyList();
        if (armyContainsMinimalSizeUnit(armyList))
            throw new MinimalUnitOperationException
                    ("Trying to remove minimal unit size");
        var lowestcreatureType = getLowestCreature(armyList);
        var lowestSlot = getSlotByCreatureType(armyList, lowestcreatureType);
        armyList.clear();
        lowestSlot.setQuantity(1);
        armyList.add(lowestSlot);
        return hero.getName() + "'s army HAS BEEN demolished";
    }

    @Transactional
    public String deleteArmySlot(String slotId, String heroId) {
        var hero = heroService.getHero(heroId);
        var army = hero.getArmyList();
        CreatureSlot deletableSlot = getSlot(army, slotId);
        army.remove(deletableSlot);
        return "Slot [" + slotId + "] of " + deletableSlot.getType() +
                " SUCC deleted from " + hero.getName() + "'s army";
    }

    @Transactional
    public String exchangeArmies(String donorId, String acceptorId) {
        var donor = heroService.getHero(donorId);
        var acceptor = heroService.getHero(acceptorId);
        var bufferArmy = acceptor.getArmyList();
        acceptor.setArmyList(donor.getArmyList());
        donor.setArmyList(bufferArmy);
        return "Armies have been swapped";
    }

    public CreatureType getLowestCreature(String heroId) {
        var hero = heroService.getHero(heroId);
        var heroArmy = hero.getArmyList();
        return discoverLowestLevelCreatureType(heroArmy);
    }
    public CreatureType getLowestCreature(List<CreatureSlot> armyList) {
        return discoverLowestLevelCreatureType(armyList);
    }

    @Transactional
    public String splitAndDistribute(SplitReqDto dto) {
        var slotId = dto.slotId();
        var heroId = dto.heroId();
        var hero = heroService.getHero(heroId);
        var army = hero.getArmyList();
        var slot = getSlot(army, slotId);
        var freeSlotsCount = ARMY_SLOT_MAX_QTY - army.size();
        if (freeSlotsCount == 0) throw new ArmyFreeSlotOverflowException("No free Slots for Splitting");
        var slotQuantity = slot.getQuantity();
        var distributionNumber = slotQuantity / ++freeSlotsCount;
        var remainder = slotQuantity % freeSlotsCount;
        IntStream.range(0, freeSlotsCount)
                .mapToObj(i -> {
                    var newSlot = new CreatureSlot();
                    newSlot.setType(slot.getType());
                    newSlot.setQuantity(distributionNumber);
                    newSlot.setModifiableDataMap(slot.getModifiableDataMap());
                    return newSlot;
                })
                .forEach(army::add);
        slot.setQuantity(distributionNumber + remainder);
        return "Slot has been distributed in " + freeSlotsCount + " slots by elements "
                + distributionNumber + " with remainder " + remainder;
        //todo redistribute remainder evenly by slots (avoid 100 = 16 + 6 x 14)
        //todo must be 15,15,14,14,14,14,14
    }

    @Transactional
    public String reinforceArmy(ReinforceReqDto dto) {
        var hero = heroService.getHero(dto.heroCode());
        var currentArmyList = hero.getArmyList();
        var reqArmyList = dto.armyList();
        var requestedArmyList =
                upgradeArmySkillToHero(reqArmyList, hero);
        if (currentArmyList == null) {
            hero.setArmyList(requestedArmyList);
        } else {
            int freeSlotsNumber = ARMY_SLOT_MAX_QTY - currentArmyList.size();
            if (freeSlotsNumber <
                    requestedArmyList.size()) {
                throw new
                        ArmyFreeSlotOverflowException("Requested reinforcent comprises of "
                        + reqArmyList.size() + " unit(s), while " + hero.getName() + " has "
                        + freeSlotsNumber + " vacant slots");
            } else {
                currentArmyList.addAll(requestedArmyList);
            }
        }
        return String.format("%s's army has been reinforced with %d units",
                hero.getName(), requestedArmyList.size());
    }

    @Transactional
    public String splitSlot(SplitReqDto dto) {
        var requestedQuantity = dto.splitQuantity();
        var hero = heroService.getHero(dto.heroId());
        var army = hero.getArmyList();
        if (army.size() == ARMY_SLOT_MAX_QTY) throw new ArmyFreeSlotOverflowException
                ("Split FAILED, no free slots");
        var slot = getSlot(army, dto.slotId());
        var currentQuantity = slot.getQuantity();
        if (currentQuantity <= requestedQuantity) {
            throw new ArmyFreeSlotOverflowException
                    ("Split IMPOSSIBLE: current quantity is LESS or EQUAL than REQUESTED");
        }
        int difference = currentQuantity - requestedQuantity;
        var newSlot = new CreatureSlot();
        newSlot.setModifiableDataMap(slot.getModifiableDataMap());
        newSlot.setQuantity(requestedQuantity);
        army.add(newSlot);
        slot.setQuantity(difference);
        return "Slot " + dto.slotId() + " has been SUCC split to " +
                difference + " and " + requestedQuantity;
    }

    private static CreatureSlot getSlot(List<CreatureSlot> army, String dto) {
        return army
                .stream()
                .filter(s -> s.getSlotId()
                        .equals(dto))
                .findFirst()
                .orElseThrow(getExceptionSupplier(CreatureSlot.class,
                        dto, EntityNotFoundException::new));
    }

    @Transactional
    public String transferArmy(String donorId, String acceptorId) {
        var acceptor = heroService.getHero(acceptorId);
        var acceptorArmy = acceptor.getArmyList();
        var acceptorArmyFreeSlotsNumber = ARMY_SLOT_MAX_QTY - acceptorArmy.size();
        if (acceptorArmyFreeSlotsNumber == 0)
            throw new ArmyFreeSlotOverflowException("Acceptor has no freeSlots");
        var donor = heroService.getHero(donorId);
        var donorArmy = donor.getArmyList();
        if (armyContainsMinimalSizeUnit(donorArmy))
            throw new MinimalUnitOperationException("Hero cannot transfer minimal unit size");
        var lowestLevelCreatureType = discoverLowestLevelCreatureType(donorArmy);
        var lowestLevelCreatureSlot = getSlotByCreatureType(donorArmy, lowestLevelCreatureType);
        mergeArmies(donorArmy, acceptorArmy, lowestLevelCreatureSlot, acceptorArmyFreeSlotsNumber, donorId);
        return acceptor.getName() + "'s army has been resupplied from " + donor.getName();
    }

    private static boolean armyContainsMinimalSizeUnit(List<CreatureSlot> donorArmy) {
        return donorArmy.size() == 1 && donorArmy.get(0).getQuantity() == 1;
    }

    private void mergeArmies(List<CreatureSlot> donorArmy,
                                    List<CreatureSlot> acceptorArmy,
                                    CreatureSlot lowestLevelCreatureSlot,
                                    int acceptorArmyFreeSlotsNumber,
                             String donorId) {
        var donorArmyAfterMergeList = new ArrayList<CreatureSlot>();
        for (int i = 0; i < donorArmy.size() && i < acceptorArmyFreeSlotsNumber; i++) {
            compressArmy(donorId);
            CreatureSlot acceptorSlot;
            try {
                acceptorSlot = acceptorArmy.get(i);
            } catch (IndexOutOfBoundsException e) {
                log.error("::Acceptor slot is NULL");
                acceptorSlot = null;
            }
            var donorSlot = donorArmy.get(i);
            if (acceptorSlot != null && donorSlot
                    .getType()
                    .equals(acceptorSlot.getType())) { // якщо слоти з однаковим типом істот
                if (donorSlot.getSlotId()
                        .equals(lowestLevelCreatureSlot.getSlotId())) { //якщо це слот з мінімальним рівнем істоти
                    acceptorSlot.setQuantity(acceptorSlot.getQuantity()
                            + donorSlot.getQuantity() - 1);
                    donorSlot.setQuantity(1);
                    donorArmyAfterMergeList.add(donorSlot);
                }
            } else { //істоти в слотах різні, або у отримувача порожній слот. Просто "переносимо" істоти донора до отримувача
                var newSlot = new CreatureSlot();
                newSlot.setQuantity(donorSlot.getQuantity() - 1);
                newSlot.setType(donorSlot.getType());
                newSlot.setModifiableDataMap(donorSlot.getModifiableDataMap());
                acceptorArmy.add(newSlot);
                //todo так само залишаємо юніт з 1 істоти
                donorSlot.setQuantity(1);
                donorArmyAfterMergeList.add(donorSlot);
            }
        }
        donorArmy.clear();
        donorArmy.addAll(donorArmyAfterMergeList);
        //todo працює неправильно.
        //todo донор (арх 1 + піхота 500 + арх 500) ==> отримувач (
    }

    private static CreatureType discoverLowestLevelCreatureType(
            List<CreatureSlot> armyList) {
        var allLowestCreatureMap = new HashMap<CreatureType, Integer>();

        //todo отримуємо мапу констант з енумів всіх істот типу з рівнем і агрегуємо
        // всіх виявлених у армії у загальну мапу
        armyList
                .forEach(slot -> {
                        var levelCreatureMap = getLevelCreatureTypeMap(slot.getType());
                        levelCreatureMap.entrySet()
                                .stream()
                                .filter(v -> v.getValue().equals(slot.getType()))
                                .forEach(k -> allLowestCreatureMap.put(k.getValue(), k.getKey()));
                        });
        //todo сортуємо цю мапу по значенню (яка тут вже LEVEL) і беремо перший-ліпший тип істоти.
        // Тобто видасть випадкове значення, яке буде першим у values(), навіть якщо це не є factionwise
        return allLowestCreatureMap
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException
                        ("Cannot find minimal Level Creature"));
    }

    @SuppressWarnings("unchecked")
    private static Map<Integer, CreatureType> getLevelCreatureTypeMap(
            CreatureType type) {
        var enumm = CreatureTypeRegistry
                .findEnumClassByChildName(type,
                        CreatureType.class);
        return CreatureTypeRegistry
                .convertEnumToLevelMap((Class) enumm);
    }

    public List<CreatureSlot> viewArmy(String heroId) {
        return heroService.getHero(heroId).getArmyList();
    }

    public List<CreatureSlotRespDto> viewArmyShort(String heroId) {
        return armyMapper.toDtoList(viewArmy(heroId));
    }

    private List<CreatureSlot> upgradeArmySkillToHero(
            List<CreatureSlot> armyList,
            Hero hero) {
        armyList.forEach(armyslot -> {
                    var modifiableMap = new HashMap<ModifiableSkill, ModifiableData>();
                    var skillEnums = ModifiableSkill.values();
                    var regularCreatureSkillMap =
                            creatureService.fetchCreatureByType(armyslot.getType())
                                    .getCreatureSkillMap();
                    for (ModifiableSkill skill : skillEnums) {
                        var regularSkillValue = regularCreatureSkillMap
                                .get(EnumMapper
                                        .map(skill, CreatureSkill.class)
                                        .orElseThrow(() -> new EnumMappingErrorException
                                                ("Error mapping to " +
                                                        CreatureSkill.class.getSimpleName())))
                                .get(0);
                        modifiableMap.put(skill, new ModifiableData(
                                        regularSkillValue.value(),
                                        getModifiedValue(hero, skill, regularSkillValue)
                                )
                        );
                    }
                    armyslot.setModifiableDataMap(modifiableMap);
                }
        );
        return armyList;
    }

    private static int getModifiedValue(Hero hero,
                                        ModifiableSkill skill,
                                        CreatureSkillValue regularSkillValue) {
        var heroPrimarySkill = getPrimarySkill(skill);
        var modifiedValue = heroPrimarySkill.isPresent() ? hero.getPrimarySkillMap()
                .get(heroPrimarySkill.get()) : 0;
        return regularSkillValue.value() + modifiedValue;
    }

    private static Optional<PrimarySkill> getPrimarySkill(ModifiableSkill skill) {
        return EnumMapper.map(skill, PrimarySkill.class);
    }

    private static CreatureSlot getSlotByCreatureType(
            List<CreatureSlot> army, CreatureType type) {
        return army
                .stream()
                .filter(s -> s.getType()
                        .equals(type))
                .findFirst()
                .orElseThrow(getExceptionSupplier(CreatureSlot.class,
                        type, EntityNotFoundException::new));
    }
}
