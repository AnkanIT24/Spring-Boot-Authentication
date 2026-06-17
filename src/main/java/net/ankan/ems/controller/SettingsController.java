package net.ankan.ems.controller;

import lombok.AllArgsConstructor;
import net.ankan.ems.dto.SettingsDto;
import net.ankan.ems.service.SettingsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("*")
@AllArgsConstructor
@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    // GET /api/settings
    @GetMapping
    public ResponseEntity<SettingsDto> getSettings() {
        SettingsDto settings = settingsService.getSettings();
        return ResponseEntity.ok(settings);
    }

    // PUT /api/settings
    @PutMapping
    public ResponseEntity<SettingsDto> updateSettings(@RequestBody SettingsDto settingsDto) {
        SettingsDto updated = settingsService.updateSettings(settingsDto);
        return ResponseEntity.ok(updated);
    }
}