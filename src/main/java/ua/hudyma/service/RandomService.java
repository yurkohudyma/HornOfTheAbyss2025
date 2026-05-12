package ua.hudyma.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import ua.hudyma.domain.creatures.converter.CreatureTypeRegistry;
import ua.hudyma.domain.creatures.dto.CreatureSlot;
import ua.hudyma.domain.creatures.enums.creaturetypes.CoveCreatureType;
import ua.hudyma.domain.heroes.Hero;
import ua.hudyma.domain.heroes.dto.HeroRespDto;
import ua.hudyma.domain.heroes.dto.HeroSpecialty;
import ua.hudyma.domain.heroes.enums.HeroFaction;
import ua.hudyma.domain.heroes.enums.HeroSpecialtyType;
import ua.hudyma.domain.heroes.enums.SecondarySkill;
import ua.hudyma.domain.players.Player;
import ua.hudyma.domain.players.dto.PlayerRespDto;
import ua.hudyma.domain.players.enums.PlayerColour;
import ua.hudyma.domain.spells.converter.SpellRegistry;
import ua.hudyma.enums.WarMachine;
import ua.hudyma.mapper.HeroMapper;
import ua.hudyma.mapper.PlayerMapper;
import ua.hudyma.resource.enums.ResourceType;
import ua.hudyma.util.IdGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static ua.hudyma.util.IdGenerator.getRandomEnum;
import static ua.hudyma.util.IdGenerator.getThreadLocalRandomIndex;

@Service
@RequiredArgsConstructor
@Log4j2
public class RandomService {

    private final PlayerMapper playerMapper;
    private final HeroMapper heroMapper;

    public String getRandomCreature(HeroFaction heroFaction) {
        var faction = heroFaction.getFaction();
        var allFactionCreatures = CreatureTypeRegistry.getAllCreaturesByFaction(faction, true);
        return allFactionCreatures[IdGenerator.getThreadLocalRandomIndex(0, allFactionCreatures.length)].getCode();
    }

    public List<CreatureSlot> generateRandomArmy(HeroFaction heroFaction) {
        if (heroFaction == null) {
            throw new IllegalArgumentException("HeroFaction is null");
        }
        var allFactionCreatures = CreatureTypeRegistry.getAllCreaturesByFaction(heroFaction.getFaction(), true);
        var armyList = new ArrayList<CreatureSlot>();
        var levelCounter = new AtomicInteger(1);
        var quantityCounter = new AtomicInteger(30);
        while (levelCounter.get() < 4) {
            var creatures = Arrays
                    .stream(allFactionCreatures)
                    .filter(creatureType -> creatureType.getLevel() == levelCounter.getAndIncrement())
                    .map(creatureType -> new CreatureSlot(
                            creatureType,
                            getThreadLocalRandomIndex(
                                    quantityCounter.get() - 10,
                                    quantityCounter.getAndUpdate(i -> i - 10))))
                    .findAny()
                    .orElse(new CreatureSlot(CoveCreatureType.HASPID, 1));
            armyList.add(creatures);
        }
        return armyList;
    }

    public HeroRespDto createRandomHeroDto() {
        var hero = new Hero();
        hero.setPlayer(generateRandomPlayer());
        hero.setName(IdGenerator.generateName());
        return heroMapper.toDto(hero);
    }

    public Hero createRandomHero() {
        var hero = new Hero();
        hero.setName(IdGenerator.generateName());
        var randomFaction = IdGenerator.getRandomEnum(HeroFaction.class);
        hero.setFaction(randomFaction);
        assignRandomHeroSpecialty(hero);
        return hero;
    }

    private void assignRandomHeroSpecialty(Hero hero) {
        var randomSpecialtyType = getRandomEnum(HeroSpecialtyType.class);
        var specialtyProperty = populateSpecialtyWithProperty(randomSpecialtyType, hero.getFaction());
        hero.setHeroSpecialty(new HeroSpecialty(randomSpecialtyType, specialtyProperty));

    }
    private Object populateSpecialtyWithProperty(HeroSpecialtyType randomSpecialtyType, HeroFaction faction) {
        return switch (randomSpecialtyType) {
            case SECONDARY_SKILL -> getRandomEnum(SecondarySkill.class).name();
            case SPEED -> 2;
            case SPELL -> {
                var index = getThreadLocalRandomIndex(1, 5);
                yield SpellRegistry.generateRandomSpell(index);
            }
            case UPGRADE ->
                    ""; //Enchanters from Monks/Zealots/Magi/Arch Magi. ###  Sea Dogs from Pirates and Corsairs. ###  Sharpshooters from Archers/Marksmen/Wood Elves/Grand Elves
            case CREATURE ->
                    getRandomCreature(faction); // Increases Speed of creatures and their Attack and Defense skills for every x levels (rounded up)
            case RESOURCE -> getRandomEnum(ResourceType.class).name();
            case WAR_MACHINE -> getRandomEnum(WarMachine.class).name();
        };
    }

    //@Transactional(readOnly = true)
    public List<PlayerRespDto> generateRandomPlayers(Integer qty) {
        qty = qty < 1 ? 1 : qty;
        qty = qty > 7 ? 7 : qty;
        var playerList = IntStream.range(0, qty)
                .mapToObj(this::generatePlayer)
                .toList();
        return playerMapper.toDtoList(playerList);
    }

    public Player generateRandomPlayer() {
        return generatePlayer(0);
    }

    private Player generatePlayer(int colourIndex) {
        var player = new Player();
        player.setName(IdGenerator.generateName());
        player.setPlayerColour(PlayerColour.values()[colourIndex]);
        var hero = createRandomHero();
        var army = generateRandomArmy(hero.getFaction()); //todo implement
        hero.setArmyList(army);
        player.getHeroList().add(hero);
        hero.setPlayer(player);
        return player;
    }

}
