package net.ankan.ems.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDto {

    private Long id;
    private boolean animationsEnabled;
    private boolean compactMode;
}