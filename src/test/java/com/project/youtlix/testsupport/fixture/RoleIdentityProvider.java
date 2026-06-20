package com.project.youtlix.testsupport.fixture;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.authentication.domain.model.ViewerId;

import java.util.UUID;

public final class RoleIdentityProvider implements IdentityProvider {

    private final UserIdentity identity;

    public RoleIdentityProvider(Role role) {
        this(UUID.randomUUID(), role);
    }

    public RoleIdentityProvider(UUID viewerId, Role role) {
        this.identity = new UserIdentity(new ViewerId(viewerId), role);
    }

    @Override
    public UserIdentity currentIdentity(String jwt) {
        return identity;
    }

    @Override
    public boolean verify(String jwt) {
        return true;
    }

    public ViewerId viewerId() {
        return identity.viewerId();
    }
}
