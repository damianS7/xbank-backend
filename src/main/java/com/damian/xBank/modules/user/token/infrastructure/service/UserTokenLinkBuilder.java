package com.damian.xBank.modules.user.token.infrastructure.service;

import com.damian.xBank.shared.utils.LinkBuilder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class UserTokenLinkBuilder {

    private final Environment env;
    private final LinkBuilder linkBuilder;

    public UserTokenLinkBuilder(
            Environment env,
            LinkBuilder linkBuilder
    ) {
        this.env = env;
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