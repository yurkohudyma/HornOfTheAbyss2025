package ua.hudyma.domain.towns;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ua.hudyma.domain.BaseEntity;
import ua.hudyma.domain.creatures.CreatureType;
import ua.hudyma.domain.towns.enums.BuildingType;
import ua.hudyma.domain.towns.enums.CastleBuildingConfig;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;
import java.util.List;

@Entity
@Table(name = "towns")
@Data
public class Town implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Enumerated(EnumType.STRING)
    private Alignment alignment;
    @Enumerated(EnumType.STRING)
    private Faction faction;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "buildings")
    private List<BuildingType> buildingTypeList;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "creatures")
    private List<CreatureType> creatureTypeList;
    private BuildingType buildingConfig = new CastleBuildingConfig();

}
