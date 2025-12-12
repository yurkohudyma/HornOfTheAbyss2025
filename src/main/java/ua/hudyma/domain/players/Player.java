package ua.hudyma.domain.players;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.towns.Town;
import ua.hudyma.util.FixedSize;
import ua.hudyma.util.FixedSizeListDeserializer;

import java.util.List;

@Entity
@Table(name = "players")
@Data
public class Player {
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
}
