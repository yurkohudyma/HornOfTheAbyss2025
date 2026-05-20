package ua.hudyma.service.build;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.spells.converter.SpellRegistry;
import ua.hudyma.domain.spells.enums.TownBannedSpells;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.GrailBuildingType;
import ua.hudyma.exception.BuildingAlreadyExistsException;
import ua.hudyma.exception.MethodNotImplementedException;
import ua.hudyma.service.SpellService;
import ua.hudyma.service.TownService;

import static ua.hudyma.domain.towns.enums.GrailBuildingType.AURORA_BOREALIS;

@Service
@RequiredArgsConstructor
@Log4j2
public class GrailBuildingService {

    private final TownService townService;

    public String buildGrailBuilding(GrailBuildingType grailType, String townName) {
     return switch (grailType){
         case AURORA_BOREALIS -> buildConflux (townName);
         case CARNIVOROUS_PLANT,
              LODESTAR,
              SKYSHIP,
              SOUL_PRISON,
              SPIRIT_GUARDIAN,
              COLOSSUS,
              GUARDIAN_OF_EARTH,
              DEITY_OF_FIRE,
              LIGHTNING_ROD,
              WARLORDS_MONUMENT -> throw new MethodNotImplementedException("Try later");
     };
    }

    private String buildConflux(String townName) {
        var town = townService.getTown(townName);
        if (town.getUniqueBuildingSet().contains(AURORA_BOREALIS)){
            throw new BuildingAlreadyExistsException
                    ("Grail building " + AURORA_BOREALIS + " already exists in " +  townName);
        }
        var commonBuildingMap = town.getCommonBuildingMap();
        if (!commonBuildingMap.containsKey(CommonBuildingType.MAGE_GUILD)){
            return "Grail gives no benefit until you build a mage guild";
        }
        var mageGuildLevel = commonBuildingMap.get(CommonBuildingType.MAGE_GUILD);
        var bannedTownSpells = TownBannedSpells
                .valueOf(town
                        .getFaction()
                        .toString())
                .getBannedSpellsSet().stream()
                .map(String::valueOf).toList();
        var allSpellsLimitedByLevel = SpellRegistry
                .resolveAllLevelSpells(mageGuildLevel)
                .stream()
                .filter(bannedSpell -> !bannedTownSpells.contains(bannedSpell))
                .toList();
        //town.getMagicGuildSpellMap().putAll(allSpellsLimitedByLevel); //todo refactor allSpells into map <Level, Set>
        town.getUniqueBuildingSet().add(AURORA_BOREALIS.name());
        return AURORA_BOREALIS + " successfully built in " + townName;
    }

}
