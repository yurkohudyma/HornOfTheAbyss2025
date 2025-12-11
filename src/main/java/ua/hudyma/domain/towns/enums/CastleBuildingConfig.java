package ua.hudyma.domain.towns.enums;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ua.hudyma.domain.towns.AbstractBuildingType;

import java.util.EnumSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class CastleBuildingConfig extends AbstractBuildingType implements BuildingType {
    public HallType hall = BuildingType.hall;
    private FortificationType fortification = BuildingType.fortification;
    private Shipyard shipyard = BuildingType.shipyard;
    private Set<CommonBuildingType> common = EnumSet.noneOf(CommonBuildingType.class);
    private HordeBuildingType horde = HordeBuildingType.GRIFFIN_BASTION;


}
