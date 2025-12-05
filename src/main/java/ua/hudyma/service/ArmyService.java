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
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.ReinforceReqDto;
import ua.hudyma.domain.heroes.enums.PrimarySkill;
import ua.hudyma.exception.ArmyFreeSlotOverflowException;
import ua.hudyma.exception.EnumMappingErrorException;
import ua.hudyma.mapper.EnumMapper;
import ua.hudyma.repository.HeroRepository;

import java.util.*;
import java.util.stream.Collectors;

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


    /*@Transactional
    public String compressArmy(String heroId) {
        var army = heroService.getHero(heroId).getArmyList();

        int iterations = 0;
        while (armyContainsDuplicateSlot(army)) {
            for (int i = 0; i < army.size() - 1; i++) {
                var slotA = army.get(i);
                var slotB = army.get(i + 1);
                if (slotA.getType().equals(slotB.getType())){
                    mergeSlot(slotA, slotB, army);
                    iterations++;
                }
            }
        }
        return " :::: Army successfully compressed in " + iterations + " iterations";
    }

    private static boolean armyContainsDuplicateSlot(List<CreatureSlot> army) {
        return army
                .stream()
                .map(CreatureSlot::getType)
                .collect(Collectors.toSet())
                .size() != army.size();
    }

    private void mergeSlot(CreatureSlot slotA, CreatureSlot slotB, List<CreatureSlot> army) {
        slotA.setQuantity(slotB.getQuantity() + slotA.getQuantity());
        var slotBId = slotB.getSlotId();
        army.remove(slotB);
        log.info(" :::: Slot {} of {} merged with slot {}", slotA.getSlotId(), slotA.getType(), slotBId);
    }*/

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
        var heroPrimarySkill= getPrimarySkill(skill);
        var modifiedValue = heroPrimarySkill.isPresent() ? hero.getPrimarySkillMap()
                .get(heroPrimarySkill.get()) : 0;
        return regularSkillValue.value() + modifiedValue;
    }

    private static Optional<PrimarySkill> getPrimarySkill(ModifiableSkill skill) {
        return EnumMapper.map(skill, PrimarySkill.class);
    }

    //todo implement compressing units
}
