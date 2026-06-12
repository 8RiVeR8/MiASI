package com.project.youtlix.authentication.infrastructure.out.supabase;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.authentication.domain.model.ViewerId;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;

/**
 * Anti-corruption layer that maps Supabase Auth users to the internal identity model.
 */
@Component
public class SupabaseIdentityAdapter implements IdentityProvider {

    private final SupabaseAuthApi supabaseAuthApi;

    /**
     * Creates the adapter around the external Supabase Auth API.
     *
     * @param supabaseAuthApi external identity provider API
     */
    public SupabaseIdentityAdapter(SupabaseAuthApi supabaseAuthApi) {
        this.supabaseAuthApi = supabaseAuthApi;
    }

    /**
     * Returns current identity without exposing SupabaseUser to domain modules.
     */
    @Override
    public UserIdentity currentIdentity(String jwt) {
        return toUserIdentity(supabaseAuthApi.getUser(jwt));
    }

    /**
     * Delegates token verification to Supabase Auth.
     */
    @Override
    public boolean verify(String jwt) {
        return supabaseAuthApi.verify(jwt);
    }

    private UserIdentity toUserIdentity(SupabaseUser user) {
        Objects.requireNonNull(user, "Supabase user must not be null");
        return new UserIdentity(new ViewerId(user.id()), mapRole(user.role()));
    }

    private Role mapRole(String role) {
        return role != null && Role.LIBRARY_ADMIN.name().equals(role.trim().toUpperCase(Locale.ROOT))
                ? Role.LIBRARY_ADMIN
                : Role.VIEWER;
    }
}
