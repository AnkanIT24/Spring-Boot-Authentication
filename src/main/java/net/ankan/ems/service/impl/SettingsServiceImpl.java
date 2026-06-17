package net.ankan.ems.service.impl;

import lombok.AllArgsConstructor;
import net.ankan.ems.dto.SettingsDto;
import net.ankan.ems.entity.SettingsEntity;
import net.ankan.ems.mapper.SettingsMapper;
import net.ankan.ems.repository.SettingsRepository;
import net.ankan.ems.service.SettingsService;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SettingsServiceImpl implements SettingsService {

    private static final Long SETTINGS_ID = 1L;

    private final SettingsRepository settingsRepository;

    @Override
    public SettingsDto getSettings() {
        SettingsEntity entity = settingsRepository.findById(SETTINGS_ID)
                .orElseGet(this::seedDefaultSettings);
        return SettingsMapper.mapToSettingsDto(entity);
    }

    @Override
    public SettingsDto updateSettings(SettingsDto settingsDto) {
        // Always target the single row
        settingsDto.setId(SETTINGS_ID);
        SettingsEntity entity = SettingsMapper.mapToSettingsEntity(settingsDto);
        SettingsEntity saved = settingsRepository.save(entity);
        return SettingsMapper.mapToSettingsDto(saved);
    }

    // ------------------------------------------------------------------
    // Private helpers
    // ------------------------------------------------------------------

    private SettingsEntity seedDefaultSettings() {
        SettingsEntity defaults = new SettingsEntity();
        defaults.setId(SETTINGS_ID);
        defaults.setAnimationsEnabled(true);
        defaults.setCompactMode(false);
        return settingsRepository.save(defaults);
    }
}