package com.damian.xBank.modules.setting.repository;

import com.damian.xBank.shared.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Optional<Setting> findByUser_Id(Long userId);
}

