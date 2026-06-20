package com.project.youtlix.testsupport.fixture;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.UserIdentity;

/**
 * Returns a fixed identity for unit tests without calling Supabase.
 */
public final class FixedIdentityProvider implements IdentityProvider {

    private final UserIdentity identity;

    public FixedIdentityProvider(UserIdentity identity) {
        this.identity = identity;
    }

    @Override
    public UserIdentity currentIdentity(String jwt) {
        return identity;
    }

    @Override
    public boolean verify(String jwt) {
        return true;
    }
}
