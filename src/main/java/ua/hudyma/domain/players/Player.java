package ua.hudyma.domain.players;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import ua.hudyma.domain.BaseEntity;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.resource.enums.MineType;
import ua.hudyma.resource.enums.ResourceType;
import ua.hudyma.util.FixedSize;
import ua.hudyma.util.FixedSizeListDeserializer;
import ua.hudyma.util.FixedSizeMap;

import java.util.*;

@Entity
@Table(name = "players")
@Data
public class Player implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;

    @JsonDeserialize(using = FixedSizeListDeserializer.class)
    @FixedSize(8)
    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL)
    @ToString.Exclude
    private List<Hero> heroList;

    @OneToMany(mappedBy = "player")
    @ToString.Exclude
    private List<Town> townsList;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "resource_map")
    @ToString.Exclude
    private Map<ResourceType, Integer> resourceMap =
            new FixedSizeMap<>(new HashMap<>(),
                    ResourceType.values().length);

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "json", name = "mines_map")
    @ToString.Exclude
    private Map<MineType, Integer> minesMap =
            new EnumMap<>(MineType.class);

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return Objects.equals(id, player.id) &&
                Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
