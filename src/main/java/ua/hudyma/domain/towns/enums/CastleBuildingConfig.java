package ua.hudyma.domain.towns.enums;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.hudyma.domain.towns.AbstractBuildingConfig;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static ua.hudyma.domain.towns.enums.HordeBuildingType.GRIFFIN_BASTION;

@EqualsAndHashCode(callSuper = true)
@Data
public class CastleBuildingConfig extends AbstractBuildingConfig implements InitialBuildingConfig {
    protected HallType hall = InitialBuildingConfig.hall;
    protected FortificationType fortification =
            InitialBuildingConfig.fortification;
    protected Shipyard shipyard = InitialBuildingConfig.shipyard;
    protected Set<CommonBuildingType> commonBuildingSet = EnumSet
            .noneOf(CommonBuildingType.class);
    protected List<HordeBuildingType> hordeBuildingList = new ArrayList<>(
            List.of(GRIFFIN_BASTION));
    protected GrailBuildingType grailBuilding = GrailBuildingType.COLOSSUS;


}
