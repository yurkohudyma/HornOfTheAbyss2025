package ua.hudyma.domain.towns.config;

import jakarta.persistence.Transient;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.hudyma.domain.towns.config.AbstractBuildingConfig;
import ua.hudyma.domain.towns.enums.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static ua.hudyma.domain.towns.enums.HordeBuildingType.GRIFFIN_BASTION;

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
    protected List<HordeBuildingType> hordeBuildingList = new ArrayList<>(
            List.of(GRIFFIN_BASTION));
    @Transient
    protected GrailBuildingType grailBuilding = GrailBuildingType.COLOSSUS;


}
