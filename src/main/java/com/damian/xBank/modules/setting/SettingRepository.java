package com.damian.whatsapp.modules.setting;

import com.damian.whatsapp.shared.domain.Setting;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface SettingRepository extends JpaRepository<Setting, Long> {
    Set<Setting> findByUser_Id(Long userId);
}

