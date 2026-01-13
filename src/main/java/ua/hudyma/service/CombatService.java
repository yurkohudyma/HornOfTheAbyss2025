package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.enums.AttackType;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.enums.AttackResultDto;
import ua.hudyma.enums.BattleResultDto;
import ua.hudyma.util.MessageProcessor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ua.hudyma.domain.creatures.enums.ModifiableSkill.DAMAGE;
import static ua.hudyma.domain.creatures.enums.ModifiableSkill.HEALTH;

@Service
@RequiredArgsConstructor
@Log4j2
public class CombatService {
    private final HeroService heroService;
    private final CreatureService creatureService;
    private final BattlefieldService battlefieldService;

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

    private AttackResultDto attackSlot(CreatureSlot attackerSlot, CreatureSlot defenderSlot) {
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
        var defenderOverallHealth = defenderHealth * attackerCount;
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
            var survivedDefenderCreaturesCount = (defenderOverallHealth - attackerOverallDamage) / defenderHealth;
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
            if (defender.getArmyList().size() >= 1) {
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
            if (attacker.getArmyList().size() >= 1) {
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
