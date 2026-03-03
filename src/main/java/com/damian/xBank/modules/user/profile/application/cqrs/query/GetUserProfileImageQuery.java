package com.damian.xBank.modules.user.profile.application.cqrs.query;

import jakarta.validation.constraints.Positive;

public record GetUserProfileImageQuery(
    @Positive
    Long userId
) {
}
