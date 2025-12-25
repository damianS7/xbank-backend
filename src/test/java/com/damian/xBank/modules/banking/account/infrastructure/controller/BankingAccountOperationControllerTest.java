package com.damian.xBank.modules.banking.account.infrastructure.controller;

import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountRole;
import com.damian.xBank.modules.user.account.account.domain.enums.UserAccountStatus;
import com.damian.xBank.modules.user.customer.domain.entity.Customer;
import com.damian.xBank.modules.user.customer.domain.enums.CustomerGender;
import com.damian.xBank.shared.AbstractControllerTest;
import org.junit.jupiter.api.BeforeEach;

import java.time.LocalDate;

public class BankingAccountOperationControllerTest extends AbstractControllerTest {

    private Customer customerA;
    private Customer customerB;
    private Customer admin;

    @BeforeEach
    void setUp() {
        customerA = Customer.create()
                            .setEmail("customer@demo.com")
                            .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                            .setFirstName("David")
                            .setLastName("Brow")
                            .setBirthdate(LocalDate.now())
                            .setPhotoPath("avatar.jpg")
                            .setPhone("123 123 123")
                            .setPostalCode("01003")
                            .setAddress("Fake ave")
                            .setCountry("US")
                            .setGender(CustomerGender.MALE);
        customerA.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(customerA);

        customerB = Customer.create()
                            .setEmail("customerB@demo.com")
                            .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                            .setFirstName("David")
                            .setLastName("Brow")
                            .setBirthdate(LocalDate.now())
                            .setPhotoPath("avatar.jpg")
                            .setPhone("123 123 123")
                            .setPostalCode("01003")
                            .setAddress("Fake ave")
                            .setCountry("US")
                            .setGender(CustomerGender.MALE);
        customerB.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(customerB);

        admin = Customer.create()
                        .setEmail("admin@demo.com")
                        .setPassword(passwordEncoder.encode(RAW_PASSWORD))
                        .setFirstName("David")
                        .setLastName("Brow")
                        .setBirthdate(LocalDate.now())
                        .setPhotoPath("avatar.jpg")
                        .setPhone("123 123 123")
                        .setPostalCode("01003")
                        .setAddress("Fake ave")
                        .setCountry("US")
                        .setRole(UserAccountRole.ADMIN)
                        .setGender(CustomerGender.MALE);
        admin.getAccount().setAccountStatus(UserAccountStatus.VERIFIED);
        customerRepository.save(admin);
    }

}