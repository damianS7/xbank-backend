package com.damian.xBank.modules.customer;

import com.damian.xBank.modules.auth.exception.AuthenticationBadCredentialsException;
import com.damian.xBank.shared.exception.Exceptions;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomerDetailsService implements UserDetailsService {
    private final CustomerRepository customerRepository;

    public CustomerDetailsService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return loadCustomerByEmail(username);
    }

    public CustomerDetails loadCustomerByEmail(String email) throws UsernameNotFoundException {
        return customerRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new AuthenticationBadCredentialsException(
                                Exceptions.AUTH.BAD_CREDENTIALS
                        )
                );
    }
}
