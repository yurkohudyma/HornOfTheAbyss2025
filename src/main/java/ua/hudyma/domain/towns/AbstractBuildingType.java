package ua.hudyma.domain.towns;

import lombok.Getter;
import ua.hudyma.domain.towns.enums.ArtifactMerchantBuildingType;
import ua.hudyma.domain.towns.enums.CommonBuildingType;
import ua.hudyma.domain.towns.enums.HordeBuildingType;

import java.util.List;

@Getter
public abstract class AbstractBuildingType {
    public List<CommonBuildingType> commonBuildingList;
    public List<ArtifactMerchantBuildingType> artifactMerchantBuildingList;
    public List<HordeBuildingType> hordeBuildingList;


}
