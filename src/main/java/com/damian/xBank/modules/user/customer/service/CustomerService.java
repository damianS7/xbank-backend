package com.damian.xBank.modules.user.customer.service;

import com.damian.xBank.modules.user.customer.dto.request.CustomerEmailUpdateRequest;
import com.damian.xBank.modules.user.customer.dto.request.CustomerRegistrationRequest;
import com.damian.xBank.modules.user.customer.exception.CustomerEmailTakenException;
import com.damian.xBank.modules.user.customer.exception.CustomerException;
import com.damian.xBank.modules.user.customer.exception.CustomerNotFoundException;
import com.damian.xBank.modules.user.customer.repository.CustomerRepository;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.domain.UserAccount;
import com.damian.xBank.shared.exception.Exceptions;
import com.damian.xBank.shared.exception.PasswordMismatchException;
import com.damian.xBank.shared.utils.AuthHelper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public CustomerService(
            CustomerRepository customerRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder
    ) {
        this.customerRepository = customerRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Creates a new customer
     *
     * @param request contains the fields needed for the customer creation
     * @return the customer created
     * @throws CustomerException if another user has the email
     */
    public Customer createCustomer(CustomerRegistrationRequest request) {

        // check if the email is already taken
        if (emailExist(request.email())) {
            throw new CustomerEmailTakenException(
                    Exceptions.CUSTOMER.EMAIL_TAKEN
            );
        }

        UserAccount userAccount = UserAccount.create()
                                             .setEmail(request.email())
                                             .setPassword(
                                                     bCryptPasswordEncoder.encode(request.password())
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
        customer.setPhotoPath(request.photo());

        return customerRepository.save(customer);
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
            throw new CustomerNotFoundException(
                    Exceptions.CUSTOMER.NOT_FOUND
            );
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
                () -> new CustomerNotFoundException(
                        Exceptions.CUSTOMER.NOT_FOUND
                )
        );
    }

    // returns the logged customer
    public Customer getCustomer() {
        Customer currentCustomer = AuthHelper.getCurrentCustomer();
        return this.getCustomer(currentCustomer.getId());
    }

    /**
     * It checks if an email exist in the database
     *
     * @param email the email to be checked
     * @return true if the email exists, false otherwise
     */
    private boolean emailExist(String email) {
        // we search the email in the database
        return customerRepository.findByAccount_Email(email).isPresent();
    }

    /**
     * It updates the email of a customer
     *
     * @param customerId the id of the customer
     * @param email      the new email to set
     * @return the customer updated
     * @throws CustomerException if the password does not match, or if the customer does not exist
     */
    public Customer updateEmail(Long customerId, String email) {
        // we get the Customer entity so we can save at the end
        Customer customer = customerRepository.findById(customerId).orElseThrow(
                () -> new CustomerNotFoundException(
                        Exceptions.CUSTOMER.NOT_FOUND
                )
        );

        // set the new email
        customer.getAccount().setEmail(email);

        // we change the updateAt timestamp field
        customer.setUpdatedAt(Instant.now());

        // save the changes
        return customerRepository.save(customer);
    }

    /**
     * It updates the email from the logged customer
     *
     * @param request that contains the current password and the new email.
     * @return the customer updated
     * @throws PasswordMismatchException if the password does not match
     */
    public Customer updateEmail(CustomerEmailUpdateRequest request) {
        // we extract the email from the Customer stored in the SecurityContext
        final Customer currentCustomer = AuthHelper.getCurrentCustomer();

        // Before making any changes we check that the password sent by the customer matches the one in the entity
        AuthHelper.validatePassword(currentCustomer.getAccount(), request.currentPassword());

        return this.updateEmail(currentCustomer.getId(), request.newEmail());
    }
}
