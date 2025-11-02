package com.damian.xBank.modules.user.customer.dto.mapper;

import com.damian.xBank.modules.user.account.account.dto.mapper.UserAccountDtoMapper;
import com.damian.xBank.modules.user.account.account.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.account.account.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.customer.dto.response.CustomerDetailDto;
import com.damian.xBank.modules.user.customer.dto.response.CustomerDto;
import com.damian.xBank.shared.domain.Customer;
import com.damian.xBank.shared.exception.Exceptions;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public class CustomerDtoMapper {
    public static CustomerDto toCustomerDto(Customer customer) {
        return new CustomerDto(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone(),
                customer.getBirthdate(),
                customer.getGender(),
                customer.getPhotoPath(),
                customer.getAddress(),
                customer.getPostalCode(),
                customer.getCountry(),
                customer.getNationalId(),
                customer.getUpdatedAt()
        );
    }

    public static CustomerDetailDto toCustomerWithAccountDto(Customer customer) {
        UserAccountDto customerAccountDto = Optional.ofNullable(
                UserAccountDtoMapper.toUserDto(customer.getAccount())
        ).orElseThrow(() -> new UserAccountNotFoundException(
                Exceptions.USER.ACCOUNT.NOT_FOUND, 0L));

        //        Set<BankingAccountDto> bankingAccountsDTO = Optional.ofNullable(customer.getBankingAccounts())
        //                                                            .orElseGet(Collections::emptySet)
        //                                                            .stream()
        //                                                            .map(BankingAccountDtoMapper::toBankingAccountDTO)
        //                                                            .collect(Collectors.toSet());

        return new CustomerDetailDto(
                customer.getId(),
                customerAccountDto.role(),
                customerAccountDto.email(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getPhone(),
                customer.getBirthdate(),
                customer.getGender(),
                customer.getPhotoPath(),
                customer.getAddress(),
                customer.getPostalCode(),
                customer.getCountry(),
                customer.getNationalId(),
                customer.getUpdatedAt(),
                customer.getUpdatedAt()
        );
    }

    public static List<CustomerDto> toCustomerDTOList(List<Customer> customers) {
        return customers
                .stream()
                .map(
                        CustomerDtoMapper::toCustomerDto
                ).toList();
    }

    public static Page<CustomerDto> toCustomerDTOPage(Page<Customer> customers) {
        return customers.map(
                CustomerDtoMapper::toCustomerDto
        );
    }
}
