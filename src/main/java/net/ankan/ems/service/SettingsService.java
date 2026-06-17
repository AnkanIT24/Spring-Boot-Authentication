package net.ankan.ems.service;

import net.ankan.ems.dto.SettingsDto;

public interface SettingsService {

    /**
     * Returns the single settings record (id=1).
     * If none exists yet, seeds a default row.
     */
    SettingsDto getSettings();

    /**
     * Updates the settings record and returns the saved state.
     */
    SettingsDto updateSettings(SettingsDto settingsDto);
}