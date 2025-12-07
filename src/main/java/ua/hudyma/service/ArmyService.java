package ua.hudyma.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureSkillValue;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.dto.ModifiableData;
import ua.hudyma.domain.creatures.dto.SplitReqDto;
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.exception.ArmyFreeSlotOverflowException;
import ua.hudyma.exception.EnumMappingErrorException;
import ua.hudyma.mapper.EnumMapper;
import ua.hudyma.repository.HeroRepository;
import ua.hudyma.util.IdGenerator;

import java.util.*;
import java.util.stream.Stream;

import static ua.hudyma.util.MessageProcessor.getExceptionSupplier;

@Service
@RequiredArgsConstructor
@Log4j2
public class ArmyService {
    private final HeroService heroService;
    private final HeroRepository heroRepository;
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
        hero.getArmyList().clear();
        return hero.getName() + "'s army HAS BEEN demolished";
    }

    @Transactional
    public String deleteArmySlot(String slotId, String heroId) {
        var hero = heroService.getHero(heroId);
        var army = hero.getArmyList();
        var deletableSlot = army.stream()
                .filter(slot -> slot.getSlotId().equals(slotId))
                .findFirst()
                .orElseThrow(getExceptionSupplier(
                        CreatureSlot.class,
                        slotId,
                        EntityNotFoundException::new));
        army.remove(deletableSlot);
        return "Slot [" + slotId + "] of " + deletableSlot.getType() +
                " SUCC deleted from " + hero.getName() + "'s army";
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
                        ArmyFreeSlotOverflowException("Requested reinforcent comprises of " + reqArmyList.size() +
                        " unit(s), while " + hero.getName() + " has " + freeSlotsNumber + " vacant slots");
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
        var slot = army
                .stream()
                .filter(slott -> slott.getSlotId()
                        .equals(dto.slotId()))
                .findAny()
                .orElseThrow(getExceptionSupplier(CreatureSlot.class,
                        dto.slotId(), EntityNotFoundException::new));
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

    public List<CreatureSlot> viewArmy(String heroId) {
        return heroService.getHero(heroId).getArmyList();
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
}
