package com.damian.xBank.modules.user.profile.application.usecase.get;

import jakarta.validation.constraints.Positive;

public record GetUserProfileImageQuery(
    @Positive
    Long userId
) {
}
