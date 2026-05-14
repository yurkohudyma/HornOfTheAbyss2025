package ua.hudyma.domain.towns.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import ua.hudyma.domain.artifacts.enums.ArtifactAction;
import ua.hudyma.domain.creatures.enums.creaturetypes.InfernoCreatureType;
import ua.hudyma.domain.spells.enums.AirSpellSchool;
import ua.hudyma.domain.spells.enums.SpellAction;
import ua.hudyma.enums.Faction;

import java.util.EnumMap;
import java.util.Map;

import static ua.hudyma.domain.artifacts.enums.ArtifactAction.*;
import static ua.hudyma.domain.creatures.enums.CreatureSkill.GROWTH;
import static ua.hudyma.domain.creatures.enums.CreatureSkill.SPEED;
import static ua.hudyma.domain.heroes.HeroParams.*;
import static ua.hudyma.domain.heroes.enums.PrimarySkill.*;
import static ua.hudyma.domain.heroes.enums.SecondarySkill.NECROMANCY;
import static ua.hudyma.domain.spells.enums.EarthSpellSchool.QUICKSAND;
import static ua.hudyma.domain.spells.enums.FireSpellSchool.LAND_MINES;
import static ua.hudyma.enums.Faction.*;

@Getter
@RequiredArgsConstructor
public enum GrailBuildingType implements AbstractBuildingType {
    AURORA_BOREALIS (CONFLUX, toEnumMap(Map.of(
            /**
             * Fills the town's Mage Guild with all spells up
             * to the level of the currently built Mage Guild,
             * but does not include spells that are never
             * available for Conflux.
             */
            ALL_SPELLS, Map.of(
                    "limit_by_current_mageguild_level",
                    "exclude_town_forbidden_spells")))),
    CARNIVOROUS_PLANT (FORTRESS, toEnumMap(Map.of(
            BOOST, Map.of(
                    ATTACK, "10, defending_only",
                    DEFENSE, "10, defending_only")))),
    COLOSSUS (CASTLE, toEnumMap(Map.of(
            BOOST, Map.of (
                    MORALE, 2)))),
    DEITY_OF_FIRE (INFERNO, toEnumMap(Map.of(
            BOOST_OTH_PARAM, Map.of (
                    GROWTH, Map.of (
                            InfernoCreatureType.FAMILIAR,
                            Map.of (15, "external_dwellings_not_included")))))),
    GUARDIAN_OF_EARTH (DUNGEON, toEnumMap(Map.of(
            BOOST, Map.of (
                    POWER, "12, defending_only")))),
    LIGHTNING_ROD (FACTORY, toEnumMap(Map.of(
            /**
             * Strikes all enemies with lightning at the first round of every battle.             *
             * Damage is based on town building count.             *
             * Damage formula is 35 + n × 7 where n is the number of built buildings
             * (upgrades do not count separately) up to a maximum of 168.
             */
            COMPLEX, Map.of (SpellAction.DAMAGE, Map.of (AirSpellSchool.LIGHTING_BOLT,
                    "35 + built_dwelling_count x 7, max=168"))))),
    LODESTAR (COVE, toEnumMap(Map.of(
            MAP_MODIFIER, "movement_no_limitation",
            BOOST, Map.of(
                    ATTACK, 1,
                    DEFENSE, 1,
                    SPEED, 1),
            IGNORE_SPELL, Map.of(
                    LAND_MINES, 0,
                    QUICKSAND, 0)))),
    SKYSHIP (TOWER, toEnumMap(Map.of(
            MAP_MODIFIER, "reveal_map, cover_of_darkness_not_cancelled",
            BOOST, Map.of(
                    MAX_SPELL_POINTS, "150, defending_only, replenish_after_battle")))),
    SOUL_PRISON (NECROPOLIS, toEnumMap(Map.of(
            BOOST_OTH_PARAM, Map.of (
                    NECROMANCY, "20%")))),
    SPIRIT_GUARDIAN (RAMPART, toEnumMap(Map.of(
            BOOST, Map.of(
                    LUCK, 2)))),
    WARLORDS_MONUMENT(STRONGHOLD, toEnumMap(
            Map.of(BOOST, Map.of(
                    ATTACK, "20, defending_only"))));

    private final Faction faction;
    private final EnumMap<ArtifactAction, Object> propertiesMap;

    private static EnumMap<ArtifactAction, Object> toEnumMap(
            Map<ArtifactAction, Object> properties) {
        var map = new EnumMap<>(ArtifactAction.class);
        map.putAll(properties);
        return map;
    }

}
