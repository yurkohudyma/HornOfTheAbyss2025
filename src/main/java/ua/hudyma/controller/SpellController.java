package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.domain.spells.enums.SpellSchool;
import ua.hudyma.service.SpellService;
import ua.hudyma.service.TownService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/spells")
@RequiredArgsConstructor
class SpellController {
    private final SpellService spellService;
    private final TownService townService;

    @GetMapping
    public ResponseEntity<Set<String>> provideTownWithSpellSet(
            @RequestParam String townName,
            @RequestParam int mageGuildLevel) {
        return ResponseEntity.ok(spellService
                .randomiseSpellSet(townName, mageGuildLevel));
    }

    @GetMapping("/town")
    public ResponseEntity<Map<Integer,Set<String>>> getTownSpells(
            @RequestParam String townName) {
        return ResponseEntity.ok(spellService
                .getTownSpells(townName));
    }
    @PatchMapping("/hero")
    public ResponseEntity<Map<Integer, Set<String>>>
    learnTownSpells(
            @RequestParam String heroId,
            @RequestParam String townName){
        return ResponseEntity.ok(spellService
                .learnTownSpells(heroId, townName));
    }
    @PatchMapping("/learn")
    public ResponseEntity<Map<Integer, Set<String>>>
    learnSpell(
            @RequestParam String heroId,
            @RequestParam String spell){
        return ResponseEntity.ok(spellService
                .learnSpell(heroId, spell));
    }

    @GetMapping("/hero")
    public ResponseEntity<Map<Integer, Set<String>>>
    getHeroSpellBook(
            @RequestParam String heroId){
        return ResponseEntity.ok(spellService
                .getHeroSpellbook(heroId));

    }
    @GetMapping("/cast")
    public ResponseEntity<String> castSpell (
            @RequestParam String heroId,
            @RequestParam String spell){
        return ResponseEntity.ok(spellService
                .castSpell(heroId, spell));
    }

    @GetMapping("/calcSpellDamage")
    public ResponseEntity<int[]> calcSpellDamage(
            @RequestParam String heroId,
            @RequestParam String spellName) {
        return ResponseEntity.ok(spellService
                .calcSpellDamage(heroId, spellName));
    }

    @GetMapping("/allBySchool")
    public ResponseEntity<Set<String>> getAllSchoolSpells (
            @RequestParam String spellSchool){
        return ResponseEntity.ok(spellService
                .getAllSchoolSpells(spellSchool));
    }

    @GetMapping("/getAvailTownsForTownPortal")
    public ResponseEntity<Set<String>>
    getAvailTownsForTownPortal(@RequestParam String heroCode){
        return ResponseEntity.ok(townService
                .getAvailTownsForTownPortal(heroCode));
    }

    @RequestMapping
    void doIt(){}
}
