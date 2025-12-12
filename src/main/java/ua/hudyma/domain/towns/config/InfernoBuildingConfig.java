package ua.hudyma.domain.towns.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import ua.hudyma.domain.towns.enums.InitialBuildingConfig;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public non-sealed class InfernoBuildingConfig extends AbstractBuildingConfig implements InitialBuildingConfig {
}
