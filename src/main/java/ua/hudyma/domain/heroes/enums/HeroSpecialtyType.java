package ua.hudyma.domain.heroes.enums;

public enum HeroSpecialtyType {
    CREATURE, // 	Increases Speed of creatures and their Attack and Defense skills for every x levels (rounded up).
    RESOURCE, // 	Increases kingdom's resource production by 1 per day.
    SECONDARY_SKILL, //Receives a x% per level bonus to an secondary skill.
    SPEED, //All creatures receive +2 speed.
    SPELL, // Casts spell with increased effect
    UPGRADE, //  	Can upgrade creatures to 3rd level
    WAR_MACHINE; // Increases the Attack and Defense skills of any Ballista by 30% Horn of the Abyss for every 5 levels (rounded up).
}
