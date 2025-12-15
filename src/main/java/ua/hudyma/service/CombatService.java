package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.enums.AttackType;
import ua.hudyma.domain.creatures.enums.ModifiableSkill;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.enums.AttackResultDto;

import java.util.List;

import static ua.hudyma.domain.creatures.enums.ModifiableSkill.DAMAGE;
import static ua.hudyma.domain.creatures.enums.ModifiableSkill.HEALTH;

@Service
@RequiredArgsConstructor
@Log4j2
public class CombatService {
    private final HeroService heroService;
    private final CreatureService creatureService;

    public void initTownBattle(Hero hero, Town town) {
        throw new IllegalStateException
                ("initTownBattle :: Method not implemented");
    }

    public AttackResultDto initBattle(String attackerId, String defenderId) {
        var attacker = heroService.getHero(attackerId);
        var defender = heroService.getHero(defenderId);
        var attackerSlot = attacker.getArmyList().get(0);
        var defenderSlot = defender.getArmyList().get(0);
        if (attacker.getPlayer().equals(defender.getPlayer())) {
            throw new UnsupportedOperationException
                    ("Cannot combat between heroes of the same Player");
        }
        var shootingCreatureList = detectShootingCreatures
                (defender.getArmyList());
        AttackResultDto dto;
        if (!shootingCreatureList.isEmpty()) {
            dto = attackSlot(
                    attackerSlot,
                    shootingCreatureList.get(0));
        }
        else {
            dto = attackSlot(attackerSlot, defenderSlot);
        }
        return dto;
    }

    private AttackResultDto attackSlot(CreatureSlot attackerSlot, CreatureSlot defenderSlot) {
        var attackerDamage = attackerSlot.getModifiableDataMap()
                .get(DAMAGE).getCurrentValue();
        log.info("::: {} attacks {}",
                attackerSlot.getType(),
                defenderSlot.getType());
        var defenderHealth = defenderSlot
                .getModifiableDataMap()
                .get(HEALTH)
                .getCurrentValue();
        var attackerSlotId = attackerSlot.getSlotId();
        var defenderSlotId = defenderSlot.getSlotId();
        if (attackerDamage >= defenderHealth){
            return new AttackResultDto(
                    attackerSlotId,
                    defenderSlotId,
                  attackerSlot.getType().getCode(),
                  defenderSlot.getType().getCode(),
                  attackerDamage,
                  defenderHealth,
                  Boolean.FALSE,
                  null,
                  null,
                  null,
                    null
            );
        }
        else {
            var retaliationDamage = defenderSlot.getModifiableDataMap()
                    .get(DAMAGE).getCurrentValue();
            var attackerHealth = attackerSlot.getModifiableDataMap()
                    .get(HEALTH).getCurrentValue();
            var retaliationResult = retaliationDamage - attackerHealth;
            return new AttackResultDto(
                    attackerSlotId,
                    defenderSlotId,
                    defenderSlot.getType().getCode(),
                    attackerSlot.getType().getCode(),
                    retaliationDamage,
                    attackerHealth,
                    Boolean.TRUE,
                    defenderHealth - attackerDamage,
                    true,
                    retaliationResult,
                    retaliationResult > 0
            );
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
