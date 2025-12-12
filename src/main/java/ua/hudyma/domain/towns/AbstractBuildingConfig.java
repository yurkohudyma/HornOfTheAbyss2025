package ua.hudyma.domain.towns;

import lombok.Getter;
import ua.hudyma.domain.towns.enums.*;

import java.util.List;

@Getter
public class AbstractBuildingConfig implements InitialBuildingConfig {
    public List<CommonBuildingType> commonBuildingList;
    public List<ArtifactMerchantBuildingType> artifactMerchantBuildingList;
    public List<HordeBuildingType> hordeBuildingList;



}
