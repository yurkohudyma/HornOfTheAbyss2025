package ua.hudyma.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.hudyma.service.SpellService;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/spells")
@RequiredArgsConstructor
class SpellController {
    private final SpellService spellService;

    @GetMapping
    public ResponseEntity<Set<String>> provideTownWithSpellSet(
            @RequestParam String townName, @RequestParam int level) {
        return ResponseEntity.ok(spellService
                .randomiseSpellSet(townName, level));
    }

    @GetMapping("/town")
    public ResponseEntity<Map<Integer,Set<String>>> getTownSpells(
            @RequestParam String townName) {
        return ResponseEntity.ok(spellService
                .getTownSpells(townName));
    }
    @PatchMapping("/hero")
    public ResponseEntity<Map<Integer, Set<String>>>
    learnHeroNewSpells(
            @RequestParam String heroId,
            @RequestParam String townName){
        return ResponseEntity.ok(spellService
                .learnHeroNewSpells(heroId, townName));
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

    @GetMapping("/allBySchool")
    public ResponseEntity<Set<String>> getAllSchoolSpells (
            @RequestParam String spellSchool){
        return ResponseEntity.ok(spellService
                .getAllSchoolSpells(spellSchool));
    }
}
