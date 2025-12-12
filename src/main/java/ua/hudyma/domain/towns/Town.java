package ua.hudyma.domain.towns;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Type;
import org.hibernate.type.SqlTypes;
import ua.hudyma.domain.BaseEntity;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.config.AbstractBuildingConfig;
import ua.hudyma.domain.towns.enums.DwellingType;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;
import ua.hudyma.util.FixedSize;
import ua.hudyma.util.FixedSizeListDeserializer;

import java.util.List;

@Entity
@Table(name = "towns")
@Data
public class Town implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    @Enumerated(EnumType.STRING)
    private Alignment alignment;
    @Enumerated(EnumType.STRING)
    private Faction faction;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    @JsonDeserialize(using = FixedSizeListDeserializer.class)
    @FixedSize(7)
    @ToString.Exclude
    private List<DwellingType> dwellingTypeList;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "garrison")
    @JsonDeserialize(using = FixedSizeListDeserializer.class)
    @FixedSize(7)
    @ToString.Exclude
    private List<CreatureSlot> garrisonArmy;
    @Type(JsonType.class)
    @Column(columnDefinition = "json")
    private AbstractBuildingConfig buildingConfig;
}
