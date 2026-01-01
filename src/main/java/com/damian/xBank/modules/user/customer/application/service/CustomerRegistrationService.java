package com.damian.xBank.modules.user.customer.application.service;

import com.damian.xBank.modules.setting.domain.service.SettingDomainService;
import com.damian.xBank.modules.user.account.account.application.service.UserAccountService;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.customer.application.dto.request.CustomerRegistrationRequest;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerException;
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerRegistrationService {
    private static final Logger log = LoggerFactory.getLogger(CustomerRegistrationService.class);
    private final CustomerRepository customerRepository;
    private final UserAccountService userAccountService;
    private final SettingDomainService settingDomainService;

    public CustomerRegistrationService(
            CustomerRepository customerRepository,
            UserAccountService userAccountService,
            SettingDomainService settingDomainService
    ) {
        this.customerRepository = customerRepository;
        this.userAccountService = userAccountService;
        this.settingDomainService = settingDomainService;
    }

    /**
     * Creates a new customer
     *
     * @param request contains the fields needed for the customer creation
     * @return the customer created
     * @throws CustomerException if another user has the email
     */
    @Transactional
    public Customer registerCustomer(CustomerRegistrationRequest request) {

        UserAccount userAccount = userAccountService.createUserAccount(
                request.email(),
                request.password(),
                UserAccountRole.CUSTOMER
        );

        // we create the customer and assign the data
        Customer customer = Customer.create()
                                    .setAccount(userAccount)
                                    .setNationalId(request.nationalId())
                                    .setFirstName(request.firstName())
                                    .setLastName(request.lastName())
                                    .setPhone(request.phoneNumber())
                                    .setGender(request.gender())
                                    .setBirthdate(request.birthdate())
                                    .setCountry(request.country())
                                    .setAddress(request.address())
                                    .setPostalCode(request.zipCode())
                                    .setPhotoPath("avatar.jpg");

        // Create default settings for the new customer
        settingDomainService.initializeDefaultSettingsFor(customer.getAccount());

        // TODO usecase
        // settingRepository.save(setting);

        return customerRepository.save(customer);
    }
}
