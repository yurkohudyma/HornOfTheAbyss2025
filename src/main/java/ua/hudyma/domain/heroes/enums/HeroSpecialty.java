package ua.hudyma.domain.heroes.enums;

public record HeroSpecialty(
   HeroSpecialtyType specialtyType,
   Object property //spell = clone, war_machine = cannon, secondarySkill = navigation etc)
) {}
