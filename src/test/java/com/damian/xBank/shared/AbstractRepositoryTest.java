package com.damian.xBank.shared;


import com.damian.xBank.modules.notification.infra.repository.NotificationRepository;
import com.damian.xBank.modules.setting.infrastructure.repository.SettingRepository;
import com.damian.xBank.modules.user.account.account.infra.repository.UserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;

@DataJpaTest
public abstract class AbstractRepositoryTest {
    @Container
    @ServiceConnection
    public static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16")
            .withReuse(true);

    protected final String RAW_PASSWORD = "123456";

    @Autowired
    protected UserAccountRepository userAccountRepository;

    @Autowired
    protected SettingRepository settingRepository;

    @Autowired
    protected NotificationRepository notificationRepository;

}