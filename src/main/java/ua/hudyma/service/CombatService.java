package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.enums.AttackType;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.enums.SecondarySkill;
import ua.hudyma.domain.heroes.enums.SkillLevel;
import ua.hudyma.domain.spells.AbstractSpellSchool;
import ua.hudyma.domain.spells.converter.SpellRegistry;
import ua.hudyma.domain.spells.enums.*;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.dto.AttackResultDto;
import ua.hudyma.dto.BattleResultDto;
import ua.hudyma.dto.SpellAttackResultDto;
import ua.hudyma.dto.SpellCastCombatReqDto;
import ua.hudyma.exception.SpellCastException;
import ua.hudyma.util.MessageProcessor;

import java.util.*;

import static ua.hudyma.domain.creatures.enums.ModifiableSkill.DAMAGE;
import static ua.hudyma.domain.creatures.enums.ModifiableSkill.HEALTH;
import static ua.hudyma.domain.heroes.HeroParams.CUR_SPELL_POINTS;
import static ua.hudyma.domain.heroes.enums.SecondarySkill.*;

@Service
@RequiredArgsConstructor
@Log4j2
public class CombatService {
    private final HeroService heroService;
    private final CreatureService creatureService;
    private final BattlefieldService battlefieldService;
    private final ArmyService armyService;

    public void initTownBattle(Hero hero, Town town) {
        throw new IllegalStateException
                ("initTownBattle :: Method not implemented");
    }

    public String initBattlefield() {
        var array = battlefieldService.initBattlefieldMap();
        return "Battlefield has been SUCC initialised";
    }

    @Transactional
    @SneakyThrows
    public BattleResultDto engageBattle(String attackerId, String defenderId) {
        var attacker = heroService.getHero(attackerId);
        var defender = heroService.getHero(defenderId);
        if (attacker.getPlayer().equals(defender.getPlayer())) {
            throw new UnsupportedOperationException
                    ("Cannot combat between heroes of the same Player");
        }
        var attackerArmy = attacker.getArmyList();
        var defenderArmy = defender.getArmyList();
        var attackResultDtoList = new ArrayList<AttackResultDto>();
        while (!defenderArmy.isEmpty() || !attackerArmy.isEmpty()) {
            var attackerSlot = resolveStrongestSlot(attackerArmy);
            if (defender.getArmyList().isEmpty()) break;
            var defenderSlot = defender.getArmyList().get(0);
            var shootingCreatureList = detectShootingCreatures
                    (defender.getArmyList());
            AttackResultDto dto;
            if (!shootingCreatureList.isEmpty()) {
                dto = attackSlot(
                        attackerSlot,
                        shootingCreatureList.get(0));
            } else {
                dto = attackSlot(attackerSlot, defenderSlot);
            }
            log.info(dto);
            checkArmySizeAndVanquishHeroAtLastSlotDefeat(
                    defender, attacker, attackerSlot, defenderSlot, dto);
            attackResultDtoList.add(dto);
            Thread.sleep(2000);
        }
        log.info(attackResultDtoList);
        return new BattleResultDto(
                defenderArmy.isEmpty(),
                attackerArmy.isEmpty(),
                attackerArmy,
                defenderArmy);
    }

    private static CreatureSlot resolveStrongestSlot(List<CreatureSlot> army) {
        return army
                .stream()
                .max(Comparator.comparing(slot -> slot.getType().getLevel()))
                .orElseThrow(() -> new IllegalStateException
                        ("Strongest CreatureSlot has not been resolved"));
    }

    @Transactional
    public SpellAttackResultDto spellCast(SpellCastCombatReqDto dto) {
        var attacker = heroService.getHero(dto.attackerId());
        var defender = heroService.getHero(dto.defenderId());
        var defenderArmy = defender.getArmyList();
        var defenderSlot = armyService.getSlot(defenderArmy, dto.defendingSlotId());
        var spell = dto.spell();
        var spellSchool = dto.spellSchool();
        var spellSchoolEnumClass =
                resolveSpellSchoolEnumClass(spellSchool);
        var enumConstants = spellSchoolEnumClass.getEnumConstants();
        var spellEnum = extractSpellFromEnum(enumConstants, spell);
        var spellAction = spellEnum.getSpellAction();
        var manaCost = spellEnum.getManaCost();
        var parametersMap = heroService.getOrCreateHeroParamsMap(attacker);
        var heroSpellPoints = parametersMap.get(CUR_SPELL_POINTS);
        if (heroSpellPoints < manaCost) {
            throw new SpellCastException("Hero spell points = " + heroSpellPoints +
                    ", while spell costs " + manaCost);
        }
        return switch (spellAction) {
            case DAMAGE -> {
                parametersMap.put(CUR_SPELL_POINTS, heroSpellPoints - manaCost);
                yield attackSlotWithSpell(defenderSlot, spellEnum, spellSchool, attacker);
            }
            case MISC, BUF, DEBUF, ADVENTURE -> throw new SpellCastException
                    ("MISC, BUF, DEBUF, ADVENTURE not implemented");
        };
    }

    //todo implement summon creatures SPELL

    private AbstractSpellSchool extractSpellFromEnum(
            Enum<? extends AbstractSpellSchool>[] enumConstants, String spell) {
        return (AbstractSpellSchool) Arrays
                .stream(enumConstants)
                .filter(s -> s.name()
                        .equals(spell))
                .findFirst()
                .orElseThrow(() ->
                        new SpellCastException("Spell not found"));
    }

    private static Class<? extends Enum<? extends AbstractSpellSchool>>
    resolveSpellSchoolEnumClass(SpellSchool spellSchool) {
        return switch (spellSchool) {
            case AIR -> AirSpellSchool.class;
            case EARTH -> EarthSpellSchool.class;
            case WATER -> WaterSpellSchool.class;
            case FIRE -> FireSpellSchool.class;
        };
    }

    private SpellAttackResultDto attackSlotWithSpell(
            CreatureSlot defenderSlot,
            AbstractSpellSchool spellEnum,
            SpellSchool spellSchool,
            Hero attacker) {
        var defenderCount = defenderSlot.getQuantity();
        log.info("::: {} is cast upon {}",
                spellEnum.getName(),
                defenderSlot.getType());
        var defenderUnitHealthValue = defenderSlot
                .getModifiableDataMap()
                .get(HEALTH).getCurrentValue();
        var defenderOverallHealth = defenderUnitHealthValue * defenderCount;
        var attackerPrimarySkillMap = attacker.getPrimarySkillMap();
        var attackerSecondarySkillMap = attacker.getSecondarySkillMap();
        var dtoSkill = spellEnum.getSpellPrimarySkill();
        var attackerPrimarySpellSkillLevel = attackerPrimarySkillMap.get(dtoSkill); //POWER = 10
        if (attackerPrimarySpellSkillLevel == null) throw new IllegalArgumentException
                ("Obligatory primary Skill is NULL");
        if (attackerPrimarySpellSkillLevel == 0) attackerPrimarySpellSkillLevel = 1;
        var spellSecondarySkill = resolveSecondarySkillLevelByMagicSchool(spellSchool);
        var attackerSecSkillLevel = attackerSecondarySkillMap.get(spellSecondarySkill);
        var skillLevelModifierMap = resolveSkillLevelModifierMap(spellEnum.getModifiedValuesList());
        var spellSecSkillLevelModifier = skillLevelModifierMap.get(attackerSecSkillLevel);
        if (spellSecSkillLevelModifier == null) spellSecSkillLevelModifier = 0;
        var modifierCoefficient = spellEnum.getModifierCoefficient();
        var spellDamageValue = attackerPrimarySpellSkillLevel * modifierCoefficient + spellSecSkillLevelModifier;
        if (spellDamageValue >= defenderOverallHealth) {
            return new SpellAttackResultDto(
                    defenderSlot.getSlotId(),
                    defenderSlot.getType().getCode(),
                    defenderCount,
                    0,
                    defenderCount,
                    spellDamageValue,
                    defenderOverallHealth,
                    false,
                    0
            );
        }
        else {
            var defenderOverallHealthLeft = defenderOverallHealth - spellDamageValue;
            var survivedDefenderCount = defenderOverallHealthLeft / defenderUnitHealthValue;
            var killedDefenderCount = defenderCount - survivedDefenderCount;
            defenderSlot.setQuantity(survivedDefenderCount);
            return new SpellAttackResultDto(
                    defenderSlot.getSlotId(),
                    defenderSlot.getType().getCode(),
                    defenderCount,
                    survivedDefenderCount,
                    killedDefenderCount,
                    spellDamageValue,
                    defenderOverallHealth,
                    true,
                    defenderOverallHealthLeft
            );
        }
    }

    private static EnumMap<SkillLevel, Integer> resolveSkillLevelModifierMap(
            List<Integer> modifiersList) {
        var enumMap = new EnumMap<SkillLevel, Integer>(SkillLevel.class);
        if (modifiersList.size() != 3) throw new IllegalArgumentException
                ("SkillLevel modifier List should have size of 3");
        var skillLevelCounter = 0;
        var values = SkillLevel.values();
        for (Integer integer : modifiersList) {
            enumMap.put(values[skillLevelCounter++], integer);
        }
        return enumMap;
    }

    private static SecondarySkill resolveSecondarySkillLevelByMagicSchool(
            SpellSchool spellSchool) {
        return switch (spellSchool){
            case AIR -> AIR_MAGIC;
            case FIRE -> FIRE_MAGIC;
            case EARTH -> EARTH_MAGIC;
            case WATER -> WATER_MAGIC;
        };
    }

    private AttackResultDto attackSlot(
            CreatureSlot attackerSlot,
            CreatureSlot defenderSlot) {
        var attackerDamage = attackerSlot
                .getModifiableDataMap()
                .get(DAMAGE).getCurrentValue(); //todo attack/defense skills NOT accounted (use nullable modified values)
        var attackerCount = attackerSlot.getQuantity();
        var defenderCount = defenderSlot.getQuantity();
        var attackerOverallDamage = attackerDamage * attackerCount;
        log.info("::: {} attacks {} :::",
                attackerSlot.getType(),
                defenderSlot.getType());
        var defenderHealth = defenderSlot
                .getModifiableDataMap()
                .get(HEALTH).getCurrentValue();
        var defenderOverallHealth = defenderHealth * defenderCount;
        var attackerSlotId = attackerSlot.getSlotId();
        var defenderSlotId = defenderSlot.getSlotId();
        var attackerCreature = attackerSlot.getType().getCode();
        var defenderCreature = defenderSlot.getType().getCode();
        if (attackerOverallDamage >= defenderOverallHealth) {
            return new AttackResultDto(
                    attackerSlotId,
                    defenderSlotId,
                    attackerCreature,
                    defenderCreature,
                    attackerCount,
                    defenderCount,
                    attackerCount,
                    0,
                    0,
                    defenderCount,
                    attackerOverallDamage,
                    defenderOverallHealth,
                    Boolean.FALSE,
                    0,
                    Boolean.FALSE,
                    null,
                    null,
                    null
            );
        } else {
            var survivedDefenderCreaturesCount = (defenderOverallHealth - attackerOverallDamage) / defenderCount;
            var killedDefenderCreaturesCount = defenderCount - survivedDefenderCreaturesCount;
            defenderSlot.setQuantity(survivedDefenderCreaturesCount);
            var retaliationOverallDamage = defenderSlot.getModifiableDataMap()
                    .get(DAMAGE).getCurrentValue() * defenderCount;
            var attackerHealth = attackerSlot.getModifiableDataMap()
                    .get(HEALTH).getCurrentValue();
            var attackerOverallHealth = attackerHealth * attackerCount;
            var retaliationResult = Math.abs(retaliationOverallDamage - attackerOverallHealth);
            log.info("::: {} survives and strikes {} back :::",
                    defenderCreature, attackerCreature);
            var survivedAttackerCreaturesCount = retaliationResult / attackerHealth;
            var killedAttackerCreaturesCount = attackerCount - survivedAttackerCreaturesCount;
            attackerSlot.setQuantity(survivedAttackerCreaturesCount);
            return new AttackResultDto(
                    attackerSlotId,
                    defenderSlotId,
                    attackerCreature,
                    defenderCreature,
                    attackerCount,
                    attackerCount,
                    survivedAttackerCreaturesCount,
                    survivedDefenderCreaturesCount,
                    killedAttackerCreaturesCount,
                    killedDefenderCreaturesCount,
                    attackerOverallDamage,
                    defenderOverallHealth,
                    Boolean.TRUE,
                    defenderOverallHealth - attackerOverallDamage,
                    true,
                    retaliationOverallDamage,
                    retaliationResult,
                    attackerCount > 0
            );
        }
    }

    private static void checkArmySizeAndVanquishHeroAtLastSlotDefeat(
            Hero defender,
            Hero attacker,
            CreatureSlot attackerSlot,
            CreatureSlot defenderSlot,
            AttackResultDto dto) {
        if (!dto.defenderSurvivedAttack()) {
            if (!defender.getArmyList().isEmpty()) {
                defender.getArmyList().remove(defenderSlot); //uncomment for real removal
                log.info("Defender slot {} has fallen in attack",
                        defenderSlot.getType());
            } else {
                //heroService.vanquishHero(defender); uncomment for real removal
                log.info("Hero {} has been vanquished by defeating his last slot {}",
                        defender.getName(),
                        defenderSlot.getType());
            }
        } else if (!dto.attackedSurvivedRetaliation()) {
            if (!attacker.getArmyList().isEmpty()) {
                attacker.getArmyList().remove(attackerSlot); //uncomment for real removal
                log.info("Attacker slot {} has fallen in retaliation",
                        attackerSlot.getType());
            } else {
                //heroService.vanquishHero(attacker); uncomment for real removal
                log.info("Hero {} has been vanquished by defeating his last slot {}",
                        attacker.getName(),
                        attackerSlot.getType());
            }
        }
    }

    private List<CreatureSlot> detectShootingCreatures(List<CreatureSlot> armyList) {
        return armyList
                .stream()
                .filter(this::slotContainsShootingCreature)
                .toList();
    }

    private boolean slotContainsShootingCreature(CreatureSlot slot) {
        var attackType = creatureService
                .fetchCreatureByType(slot.getType())
                .getAttackType();
        return attackType == AttackType.SHOOTING ||
                attackType == AttackType.FIREBALL_STYLE;
    }
}
