package com.damian.xBank.modules.user.customer.application.service;

import com.damian.xBank.modules.setting.application.service.SettingService;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.entity.UserAccount;
import com.damian.xBank.modules.user.account.account.application.service.UserAccountService;
import com.damian.xBank.modules.user.customer.application.dto.request.CustomerRegistrationRequest;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerException;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.infra.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerRegistrationService {
    private static final Logger log = LoggerFactory.getLogger(CustomerRegistrationService.class);
    private final CustomerRepository customerRepository;
    private final UserAccountService userAccountService;
    private final SettingService settingService;

    public CustomerRegistrationService(
            CustomerRepository customerRepository,
            UserAccountService userAccountService,
            SettingService settingService
    ) {
        this.customerRepository = customerRepository;
        this.userAccountService = userAccountService;
        this.settingService = settingService;
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
        Customer customer = new Customer();
        customer.setAccount(userAccount);
        customer.setNationalId(request.nationalId());
        customer.setFirstName(request.firstName());
        customer.setLastName(request.lastName());
        customer.setPhone(request.phoneNumber());
        customer.setGender(request.gender());
        customer.setBirthdate(request.birthdate());
        customer.setCountry(request.country());
        customer.setAddress(request.address());
        customer.setPostalCode(request.zipCode());
        customer.setPhotoPath("avatar.jpg");

        // Create default settings for the new customer
        settingService.createDefaultSettings(customer.getAccount());

        return customerRepository.save(customer);
    }
}
