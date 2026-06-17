package net.ankan.ems.mapper;

import net.ankan.ems.dto.SettingsDto;
import net.ankan.ems.entity.SettingsEntity;

public class SettingsMapper {

    public static SettingsDto mapToSettingsDto(SettingsEntity entity) {
        return new SettingsDto(
                entity.getId(),
                entity.isAnimationsEnabled(),
                entity.isCompactMode()
        );
    }

    public static SettingsEntity mapToSettingsEntity(SettingsDto dto) {
        SettingsEntity entity = new SettingsEntity();
        entity.setId(dto.getId());
        entity.setAnimationsEnabled(dto.isAnimationsEnabled());
        entity.setCompactMode(dto.isCompactMode());
        return entity;
    }
}