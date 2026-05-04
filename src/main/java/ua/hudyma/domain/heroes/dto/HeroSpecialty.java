package ua.hudyma.domain.heroes.dto;

import ua.hudyma.domain.heroes.enums.HeroSpecialtyType;

public record HeroSpecialty(
   HeroSpecialtyType specialtyType,
   Object property //spell = clone, war_machine = cannon)
) {}
