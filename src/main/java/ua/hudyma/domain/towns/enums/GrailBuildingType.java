package ua.hudyma.domain.towns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.artifacts.enums.ArtifactAction;
import ua.hudyma.enums.Faction;
import ua.hudyma.resource.enums.ResourceType;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.domain.artifacts.enums.ArtifactAction.*;
import static ua.hudyma.domain.creatures.enums.CreatureSkill.SPEED;
import static ua.hudyma.domain.heroes.enums.PrimarySkill.ATTACK;
import static ua.hudyma.domain.heroes.enums.PrimarySkill.DEFENSE;
import static ua.hudyma.domain.spells.enums.EarthSpellSchool.QUICKSAND;
import static ua.hudyma.domain.spells.enums.FireSpellSchool.LAND_MINES;
import static ua.hudyma.enums.Faction.*;

@Getter
@RequiredArgsConstructor
public enum GrailBuildingType implements AbstractBuildingType {
    AURORA_BOREALIS (CONFLUX, toEnumMap(Map.of())),
    CARNIVOROUS_PLANT (FORTRESS, toEnumMap(Map.of())),
    COLOSSUS (CASTLE, toEnumMap(Map.of())),
    DEITY_OF_FIRE (CONFLUX, toEnumMap(Map.of())),
    GUARDIAN_OF_EARTH (DUNGEON, toEnumMap(Map.of())),
    LIGHTNING_ROD (FACTORY, toEnumMap(Map.of())),
    LODESTAR (COVE, toEnumMap(Map.of(
            MAP_MODIFIER, "movement_no_limitation",
            BOOST, Map.of(
                    ATTACK, 1,
                    DEFENSE, 1,
                    SPEED, 1),
            IGNORE_SPELL, Map.of(
                    LAND_MINES, 0,
                    QUICKSAND, 0)))),
    SKYSHIP (TOWER, toEnumMap(Map.of())),
    SOUL_PRISON (INFERNO, toEnumMap(Map.of())),
    SPIRIT_GUARDIAN (RAMPART, toEnumMap(Map.of())),
    WARLORDS_MONUMENT(STRONGHOLD, toEnumMap(Map.of()));
    private final Faction faction;
    private final EnumMap<ArtifactAction, Object> propertiesMap;

    //todo populate the rest of GrailBuilding properties

    private static EnumMap<ArtifactAction, Object> toEnumMap(
            Map<ArtifactAction, Object> properties) {
        var map = new EnumMap<>(ArtifactAction.class);
        map.putAll(properties);
        return map;
    }

}
