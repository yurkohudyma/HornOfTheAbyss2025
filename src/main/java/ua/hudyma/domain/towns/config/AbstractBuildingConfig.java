package ua.hudyma.domain.towns.config;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.Data;
import ua.hudyma.domain.towns.enums.*;

import java.util.List;

@Data
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = CastleBuildingConfig.class, name = "castle")
})

public abstract sealed class AbstractBuildingConfig
        permits CastleBuildingConfig, InfernoBuildingConfig {
    public List<CommonBuildingType> commonBuildingList;
    public List<ArtifactMerchantBuildingType> artifactMerchantBuildingList;
    public List<HordeBuildingType> hordeBuildingList;
    public List<UniqueBuildingType> uniqueBuildingList;
    public GrailBuildingType grailBuilding;
}
