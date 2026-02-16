package ua.hudyma.domain.creatures;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ua.hudyma.domain.BaseEntity;
import ua.hudyma.domain.creatures.converter.CreatureTypeConverter;
import ua.hudyma.domain.creatures.dto.CreaturePropertyValue;
import ua.hudyma.domain.creatures.dto.CreatureSkillValue;
import ua.hudyma.domain.creatures.enums.AttackType;
import ua.hudyma.domain.creatures.enums.CreatureProperty;
import ua.hudyma.domain.creatures.enums.CreatureSkill;
import ua.hudyma.enums.Faction;

import java.util.List;
import java.util.Map;

@Entity
@Table(name = "creatures")
@Data
public class Creature implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Enumerated(EnumType.STRING)
    private Faction faction;
    @Enumerated(EnumType.STRING)
    private AttackType attackType;
    @Convert(converter = CreatureTypeConverter.class)
    private CreatureType creatureType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "creature_property_map")
    private Map<CreatureProperty, List<CreaturePropertyValue>>
            creaturePropertyMap;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "creature_skill_map")
    private Map<CreatureSkill, CreatureSkillValue>
            creatureSkillMap;
}
