package com.sportflow.features.security.domain.ports;

import com.sportflow.features.security.domain.model.AuthProvider;

public interface OAuthClientPort {
    record OAuthUserProfile(
            String providerId,
            String email,
            String name,
            String avatarUrl
    ) {}

    OAuthUserProfile verifyAndFetchProfile(AuthProvider provider, String providerToken);
    boolean supports(AuthProvider provider);
}
