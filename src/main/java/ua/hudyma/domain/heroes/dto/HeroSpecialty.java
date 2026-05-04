package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.heroes.enums.HeroSpecialtyType;

public record HeroSpecialty(
   HeroSpecialtyType specialtyType,
   String property //spell = clone, war_machine = cannon)
) {}
