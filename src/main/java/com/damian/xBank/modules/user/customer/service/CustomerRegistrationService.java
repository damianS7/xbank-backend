package com.damian.xBank.modules.user.customer.service;

import com.damian.xBank.modules.user.account.account.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.service.UserAccountService;
import com.damian.xBank.modules.user.customer.dto.request.CustomerRegistrationRequest;
import com.damian.xBank.modules.user.customer.exception.CustomerException;
import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.UserAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CustomerRegistrationService {
    private static final Logger log = LoggerFactory.getLogger(CustomerRegistrationService.class);
    private final CustomerRepository customerRepository;
    private final UserAccountService userAccountService;

    public CustomerRegistrationService(
            CustomerRepository customerRepository,
            UserAccountService userAccountService
    ) {
        this.customerRepository = customerRepository;
        this.userAccountService = userAccountService;
    }

    /**
     * Creates a new customer
     *
     * @param request contains the fields needed for the customer creation
     * @return the customer created
     * @throws CustomerException if another user has the email
     */
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
        customer.setPhone(request.phone());
        customer.setGender(request.gender());
        customer.setBirthdate(request.birthdate());
        customer.setCountry(request.country());
        customer.setAddress(request.address());
        customer.setPostalCode(request.postalCode());
        customer.setPhotoPath("avatar.jpg");

        return customerRepository.save(customer);
    }
}
