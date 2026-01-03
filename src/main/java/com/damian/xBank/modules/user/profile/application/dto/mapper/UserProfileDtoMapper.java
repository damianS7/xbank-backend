package com.damian.xBank.modules.user.profile.application.dto.mapper;

import com.damian.xBank.modules.user.user.application.dto.mapper.UserAccountDtoMapper;
import com.damian.xBank.modules.user.user.application.dto.response.UserAccountDto;
import com.damian.xBank.modules.user.user.domain.exception.UserAccountNotFoundException;
import com.damian.xBank.modules.user.profile.application.dto.response.UserProfileDetailDto;
import com.damian.xBank.modules.user.profile.application.dto.response.UserProfileDto;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public class UserProfileDtoMapper {
    public static UserProfileDto toUserProfileDto(UserProfile profile) {
        return new UserProfileDto(
                profile.getId(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getBirthdate(),
                profile.getGender(),
                profile.getPhotoPath(),
                profile.getAddress(),
                profile.getPostalCode(),
                profile.getCountry(),
                profile.getNationalId(),
                profile.getUpdatedAt()
        );
    }

    public static UserProfileDetailDto toCustomerWithAccountDto(UserProfile profile) {
        UserAccountDto userAccountDto = Optional.ofNullable(
                UserAccountDtoMapper.toUserDto(profile.getUser())
        ).orElseThrow(
                () -> new UserAccountNotFoundException(0L)
        );

        //        Set<BankingAccountDto> bankingAccountsDTO = Optional.ofNullable(user.getBankingAccounts())
        //                                                            .orElseGet(Collections::emptySet)
        //                                                            .stream()
        //                                                            .map(BankingAccountDtoMapper::toBankingAccountDTO)
        //                                                            .collect(Collectors.toSet());

        return new UserProfileDetailDto(
                profile.getId(),
                userAccountDto.role(),
                userAccountDto.email(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getPhone(),
                profile.getBirthdate(),
                profile.getGender(),
                profile.getPhotoPath(),
                profile.getAddress(),
                profile.getPostalCode(),
                profile.getCountry(),
                profile.getNationalId(),
                profile.getUpdatedAt(),
                profile.getUpdatedAt()
        );
    }

    public static List<UserProfileDto> toCustomerDTOList(List<UserProfile> users) {
        return users
                .stream()
                .map(
                        UserProfileDtoMapper::toUserProfileDto
                ).toList();
    }

    public static Page<UserProfileDto> toCustomerDTOPage(Page<UserProfile> users) {
        return users.map(
                UserProfileDtoMapper::toUserProfileDto
        );
    }
}
