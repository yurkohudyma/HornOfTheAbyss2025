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
public class SpellController {
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
    private ResponseEntity<String> learnHeroNewSpells(
            @RequestParam String heroId, @RequestParam String townName){
        return ResponseEntity.ok(spellService
                .learnHeroNewSpells(heroId, townName));
    }

    @GetMapping("/hero")
    private ResponseEntity<Map<Integer, Set<String>>> getHeroSpellBook(
            @RequestParam String heroId){
        return ResponseEntity.ok(spellService
                .getHeroSpellbook(heroId));

    }
}
