package com.damian.xBank.modules.user.token.infrastructure.service;

import com.damian.xBank.shared.utils.LinkBuilder;
import org.springframework.stereotype.Component;

@Component
public class UserTokenLinkBuilder {

    private final LinkBuilder linkBuilder;

    public UserTokenLinkBuilder(
            LinkBuilder linkBuilder
    ) {
        this.linkBuilder = linkBuilder;
    }

    public String buildPasswordResetLink(String token) {
        return linkBuilder.build(
                String.format("/accounts/password/reset/%s", token)
        );
    }

    public String buildAccountVerificationLink(String token) {
        return linkBuilder.build(
                String.format("/accounts/verify/%s", token)
        );
    }
}