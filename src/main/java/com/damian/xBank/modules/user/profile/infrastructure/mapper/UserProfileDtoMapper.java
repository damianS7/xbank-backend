package com.damian.xBank.modules.user.profile.infrastructure.mapper;

import com.damian.xBank.modules.user.profile.application.cqrs.command.UserProfileUpdateCommand;
import com.damian.xBank.modules.user.profile.application.cqrs.result.UserProfileDetailResult;
import com.damian.xBank.modules.user.profile.domain.model.UserProfile;
import com.damian.xBank.modules.user.profile.infrastructure.rest.dto.request.UserProfileUpdateRequest;
import com.damian.xBank.modules.user.profile.infrastructure.rest.dto.response.UserProfileDto;
import com.damian.xBank.modules.user.user.application.cqrs.result.UserResult;
import com.damian.xBank.modules.user.user.domain.exception.UserNotFoundException;
import com.damian.xBank.modules.user.user.infrastructure.mapper.UserDtoMapper;
import org.springframework.data.domain.Page;

import java.util.List;

public class UserProfileDtoMapper {
    public static UserProfileDto toUserProfileDto(UserProfile profile) {
        return new UserProfileDto(
            profile.getId(),
            profile.getFirstName(),
            profile.getLastName(),
            profile.getPhoneNumber(),
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

    public static UserProfileUpdateCommand toCommand(UserProfileUpdateRequest request) {
        return new UserProfileUpdateCommand(
            request.currentPassword(),
            request.fieldsToUpdate()
        );
    }

    public static UserProfileDetailResult toCustomerWithAccountDto(UserProfile profile) {
        if (profile.getUser() == null) {
            throw new UserNotFoundException(0L);
        }

        UserResult userResult = UserDtoMapper.toUserResult(profile.getUser());

        //        Set<BankingAccountDto> bankingAccountsDTO = Optional.ofNullable(user.getBankingAccounts())
        //                                                            .orElseGet(Collections::emptySet)
        //                                                            .stream()
        //                                                            .map(BankingAccountDtoMapper::toBankingAccountDTO)
        //                                                            .collect(Collectors.toSet());

        return new UserProfileDetailResult(
            profile.getId(),
            userResult.role(),
            userResult.email(),
            profile.getFirstName(),
            profile.getLastName(),
            profile.getPhoneNumber(),
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
