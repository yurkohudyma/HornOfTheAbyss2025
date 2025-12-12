package ua.hudyma.domain.towns.enums;

import java.util.List;

public interface InitialBuildingConfig {
    HallType hall = HallType.VILLAGE_HALL;
    FortificationType fortification = FortificationType.NONE;
    Shipyard shipyard = Shipyard.NONE;
    default List<Enum<?>> getInitialConstantList() {
        return List.of(hall,
                fortification,
                shipyard);
    }
}
