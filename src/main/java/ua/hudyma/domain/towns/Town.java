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
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.towns.enums.*;
import ua.hudyma.domain.towns.enums.dwelling.AbstractDwellingType;
import ua.hudyma.enums.Alignment;
import ua.hudyma.enums.Faction;
import ua.hudyma.util.FixedSize;
import ua.hudyma.util.FixedSizeListDeserializer;

import java.util.*;

@Entity
@Table(name = "towns")
@Data
public class Town implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String name;
    @OneToOne
    @JoinColumn(name = "visitor_hero_id")
    private Hero visitingHero;
    @OneToOne
    @JoinColumn(name = "garrison_hero_id")
    private Hero garrisonHero;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    @Enumerated(EnumType.STRING)
    private Alignment alignment;
    @Enumerated(EnumType.STRING)
    private Faction faction;
    /*@Type(JsonType.class)
    @Column(columnDefinition = "json")
    @JsonDeserialize(using = FixedSizeListDeserializer.class)
    @FixedSize(7)
    @ToString.Exclude
    private List<AbstractDwellingType> dwellingTypeList;*/
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "common_building_map")
    @ToString.Exclude
    private Map<CommonBuildingType, Integer> commonBuildingMap =
            new EnumMap<>(CommonBuildingType.class);

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "dwelling_map")
    @ToString.Exclude
    private Map<String, Integer> dwellingMap = new HashMap<>();
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "garrison")
    @JsonDeserialize(using = FixedSizeListDeserializer.class)
    @FixedSize(7)
    @ToString.Exclude
    private List<CreatureSlot> garrisonArmy;
    @Enumerated(EnumType.STRING)
    private HallType hallType = HallType.VILLAGE_HALL;
    @Enumerated(EnumType.STRING)
    private FortificationType fortificationType;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "unique_buildings")
    private Set<String> uniqueBuildingSet =
            new HashSet<>();
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "horde_buildings")
    private Set<String> hordeBuilding = new HashSet<>();
}
