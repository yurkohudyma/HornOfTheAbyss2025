package ua.hudyma.domain.heroes;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ua.hudyma.domain.BaseEntity;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.artifacts.enums.Artifact;
import ua.hudyma.domain.heroes.enums.*;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.util.FixedSize;
import ua.hudyma.util.FixedSizeListDeserializer;
import ua.hudyma.util.FixedSizeMap;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "heroes")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Hero implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String code;
    private String name;
    @Enumerated(EnumType.STRING)
    private HeroFaction faction;
    @Enumerated(EnumType.STRING)
    private HeroSubfaction subfaction;
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "primary_skill_map")
    @ToString.Exclude
    private Map<PrimarySkill, Integer> primarySkillMap =
            new FixedSizeMap<>(new HashMap<>(),4);
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "secondary_skill_map")
    @ToString.Exclude
    private Map<SecondarySkill, SkillLevel> secondarySkillMap =
            new FixedSizeMap<>(4, 8);
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "body_inventory_map")
    @ToString.Exclude
    private Map<ArtifactSlot, List<Artifact>> bodyInventoryMap =
            new EnumMap<>(ArtifactSlot.class);
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "misc_inventory_map")
    @ToString.Exclude
    private Map<ArtifactSlot, Artifact> miscInventoryMap =
            new FixedSizeMap<>(new HashMap<>(), 5);
    @JsonDeserialize(using = FixedSizeListDeserializer.class)
    @FixedSize(64)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "backpack_inventory_list")
    @ToString.Exclude
    private List<Artifact> backpackInventoryList;
    @JsonDeserialize(using = FixedSizeListDeserializer.class)
    @FixedSize(7)
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "army_slot_list")
    @ToString.Exclude
    private List<CreatureSlot> armyList;
    @ManyToOne
    @JoinColumn(name = "player_id")
    private Player player;
    @OneToOne(mappedBy = "visitingHero")
    @ToString.Exclude
    private Town visitingTown;
    //todo add to dto
    @OneToOne(mappedBy = "garrisonHero")
    @ToString.Exclude
    private Town garrisonTown;
    //todo add to dto
}
