package ua.hudyma.domain.towns.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.hudyma.domain.towns.enums.*;

import java.util.EnumSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public non-sealed class CastleBuildingConfig extends AbstractBuildingConfig implements InitialBuildingConfig {
    protected HallType hall = InitialBuildingConfig.hall;
    protected FortificationType fortification =
            InitialBuildingConfig.fortification;
    protected Shipyard shipyard = InitialBuildingConfig.shipyard;
    protected Set<CommonBuildingType> commonBuildingSet = EnumSet
            .noneOf(CommonBuildingType.class);
}
