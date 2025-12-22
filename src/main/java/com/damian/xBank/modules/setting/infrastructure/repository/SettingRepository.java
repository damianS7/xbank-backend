package com.damian.xBank.modules.setting.infrastructure.repository;

import com.damian.xBank.modules.setting.domain.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByUser_Id(Long userId);
}

