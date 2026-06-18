package ua.hudyma.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.creatures.dto.CreatureSkillValue;
import ua.hudyma.domain.creatures.enums.CreatureSkill;

import java.util.Map;

import static ua.hudyma.domain.creatures.enums.CreatureSkill.*;

@Getter
@RequiredArgsConstructor
public enum WarMachineProperties {
    BALLISTA(Map.of(
            ATTACK, new CreatureSkillValue(10, null),
            DEFENSE, new CreatureSkillValue(10, null),
            DAMAGE, new CreatureSkillValue(2, 3),
            HEALTH, new CreatureSkillValue(250, null),
            SIZE, new CreatureSkillValue(2, null),
            SHOTS, new CreatureSkillValue(24, null)
    )),
    AMMO_CART(Map.of(
            DEFENSE, new CreatureSkillValue(5, null),
            HEALTH, new CreatureSkillValue(100, null),
            SIZE, new CreatureSkillValue(1, null)
    )),
    CANNON(Map.of(
            ATTACK, new CreatureSkillValue(20, null),
            DEFENSE, new CreatureSkillValue(10, null),
            DAMAGE, new CreatureSkillValue(4, 7),
            HEALTH, new CreatureSkillValue(250, null),
            SIZE, new CreatureSkillValue(2, null),
            SHOTS, new CreatureSkillValue(8, null)
    )),
    FIRST_AID_TENT(Map.of(
            DEFENSE, new CreatureSkillValue(0, null),
            HEALTH, new CreatureSkillValue(75, null),
            SIZE, new CreatureSkillValue(2, null)
    )),
    CATAPULT(Map.of(
            ATTACK, new CreatureSkillValue(10, null),
            DEFENSE, new CreatureSkillValue(10, null),
            HEALTH, new CreatureSkillValue(1000, null),
            SIZE, new CreatureSkillValue(2, null),
            SHOTS, new CreatureSkillValue(24, null)
    ));

    private final Map<CreatureSkill, CreatureSkillValue>
            creatureSkillMap;
}
