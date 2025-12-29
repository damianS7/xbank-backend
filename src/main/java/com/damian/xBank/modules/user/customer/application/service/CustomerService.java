package com.damian.xBank.modules.user.customer.application.service;

import com.damian.xBank.modules.user.customer.application.dto.request.CustomerUpdateRequest;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerUpdateAuthorizationException;
import com.damian.xBank.modules.user.customer.domain.exception.CustomerUpdateException;
import com.damian.xBank.modules.user.customer.infrastructure.repository.CustomerRepository;
import com.damian.xBank.shared.security.AuthenticationContext;
import com.damian.xBank.shared.security.PasswordValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;

@Service
public class CustomerService {
    private static final Logger log = LoggerFactory.getLogger(CustomerService.class);
    private final CustomerRepository customerRepository;
    private final AuthenticationContext authenticationContext;
    private final PasswordValidator passwordValidator;

    public CustomerService(
            CustomerRepository customerRepository,
            AuthenticationContext authenticationContext,
            PasswordValidator passwordValidator
    ) {
        this.customerRepository = customerRepository;
        this.authenticationContext = authenticationContext;
        this.passwordValidator = passwordValidator;
    }

    /**
     * Deletes a customer
     *
     * @param customerId the id of the customer to be deleted
     * @return true if the customer was deleted
     * @throws CustomerException if the customer does not exist or if the logged user is not ADMIN
     */
    public boolean deleteCustomer(Long customerId) {
        // if the customer does not exist we throw an exception
        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(customerId);
        }

        // we delete the customer
        customerRepository.deleteById(customerId);

        // if no exception is thrown we return true
        return true;
    }

    /**
     * Returns all the customers
     *
     * @return a list of CustomerDTO
     * @throws CustomerException if the logged user is not ADMIN
     */
    public Page<Customer> getCustomers(Pageable pageable) {
        // we return all the customers
        return customerRepository.findAll(pageable);
    }

    /**
     * Returns a customer
     *
     * @param customerId the id of the customer to be returned
     * @return the customer
     * @throws CustomerException if the customer does not exist or if the logged user is not ADMIN
     */
    public Customer getCustomer(Long customerId) {
        // if the customer does not exist we throw an exception
        return customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException(customerId)
        );
    }

    // returns the logged customer
    public Customer getCustomer() {
        Customer currentCustomer = authenticationContext.getCurrentCustomer();
        return this.getCustomer(currentCustomer.getId());
    }

    /**
     * It updates the current customer profile
     *
     * @param request the request containing the updated profile information
     * @return Customer the updated customer
     */
    public Customer updateCustomer(CustomerUpdateRequest request) {
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        log.debug("Updating customer id: {}", currentCustomer.getId());
        return this.updateCustomer(currentCustomer.getId(), request);
    }

    /**
     * It updates the customer profile by its ID.
     *
     * @param customerId the id of the profile to be updated
     * @param request    the request containing the updated profile information
     * @return Customer with the updated profile
     * @throws CustomerNotFoundException if the profile is not found
     */
    public Customer updateCustomer(Long customerId, CustomerUpdateRequest request) {
        final Customer currentCustomer = authenticationContext.getCurrentCustomer();

        // find the customer we want to modify
        Customer customer = customerRepository
                .findById(customerId)
                .orElseThrow(
                        () -> new CustomerNotFoundException(customerId)
                );

        // we make sure that this profile belongs to the current customer
        if (!customer.getId().equals(currentCustomer.getId())) {
            throw new CustomerUpdateAuthorizationException(customerId);
        }

        // we validate the password before updating the profile
        passwordValidator.validatePassword(customer, request.currentPassword());

        // we iterate over the fields (if any)
        request.fieldsToUpdate().forEach((key, value) -> {
            switch (key) {
                case "firstName" -> customer.setFirstName((String) value);
                case "lastName" -> customer.setLastName((String) value);
                case "phoneNumber" -> customer.setPhone((String) value);
                case "country" -> customer.setCountry((String) value);
                case "zipCode" -> customer.setPostalCode((String) value);
                case "address" -> customer.setAddress((String) value);
                case "photo" -> customer.setPhotoPath((String) value);
                case "gender" -> customer.setGender(CustomerGender.valueOf((String) value));
                case "birthdate" -> customer.setBirthdate(LocalDate.parse((String) value));
                default -> throw new CustomerUpdateException(
                        customerId, new Object[]{key, value.toString()}
                );
            }
        });

        // we change the updateAt timestamp field
        customer.setUpdatedAt(Instant.now());

        // we save the updated profile to the database
        return customerRepository.save(customer);
    }
}
