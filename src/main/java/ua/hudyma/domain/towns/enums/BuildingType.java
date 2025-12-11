package ua.hudyma.domain.towns.enums;

import java.util.ArrayList;
import java.util.List;

public interface BuildingType {
    HallType hall = HallType.TOWN_HALL;
    FortificationType fortification = FortificationType.NONE;
    List<CommonBuildingType> commonBuildingList = new ArrayList<>();
    List<ArtifactMerchantBuildingType> artifactMerchantsBuildingList = new ArrayList<>();
    List<HordeBuildingType> hordeBuildingList = new ArrayList<>();
    Shipyard shipyrd = Shipyard.NONE;
    GrailBuildingType grailBuilding = GrailBuildingType.NONE;
    String getName();
}
