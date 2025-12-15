package ua.hudyma.domain.players;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ua.hudyma.domain.BaseEntity;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.util.FixedSize;
import ua.hudyma.util.FixedSizeListDeserializer;

import java.util.List;
import java.util.Objects;

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
    @OneToMany(mappedBy = "player")
    @ToString.Exclude
    private List<Hero> heroList;
    @OneToMany(mappedBy = "player")
    @ToString.Exclude
    private List<Town> townsList;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Player player)) return false;
        return Objects.equals(id, player.id) && Objects.equals(name, player.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }
}
