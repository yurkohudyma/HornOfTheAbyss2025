package ua.hudyma.domain.towns.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
public non-sealed class InfernoBuildingConfig extends AbstractBuildingConfig implements InitialBuildingConfig {
}
