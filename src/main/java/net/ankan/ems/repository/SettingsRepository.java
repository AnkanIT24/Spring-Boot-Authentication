package net.ankan.ems.repository;

import net.ankan.ems.entity.SettingsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettingsRepository extends JpaRepository<SettingsEntity, Long> {
}