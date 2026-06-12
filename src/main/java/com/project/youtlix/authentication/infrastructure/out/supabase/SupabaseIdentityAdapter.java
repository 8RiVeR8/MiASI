package com.project.youtlix.authentication.infrastructure.out.supabase;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.authentication.domain.model.ViewerId;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.Objects;
import java.util.UUID;

/**
 * Anti-corruption layer that maps Supabase Auth users to the internal identity model.
 */
@Component
public class SupabaseIdentityAdapter implements IdentityProvider {

    private final SupabaseAuthApi supabaseAuthApi;
    private final JdbcTemplate jdbcTemplate;

    /**
     * Creates the adapter around the external Supabase Auth API and profile store.
     */
    public SupabaseIdentityAdapter(SupabaseAuthApi supabaseAuthApi, JdbcTemplate jdbcTemplate) {
        this.supabaseAuthApi = supabaseAuthApi;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public UserIdentity currentIdentity(String jwt) {
        return toUserIdentity(supabaseAuthApi.getUser(jwt));
    }

    @Override
    public boolean verify(String jwt) {
        return supabaseAuthApi.verify(jwt);
    }

    private UserIdentity toUserIdentity(SupabaseUser user) {
        Objects.requireNonNull(user, "Supabase user must not be null");
        return new UserIdentity(new ViewerId(user.id()), resolveRole(user.id()));
    }

    private Role resolveRole(UUID userId) {
        try {
            String role = jdbcTemplate.queryForObject(
                    "SELECT role::text FROM identity.user_profiles WHERE id = ?",
                    String.class,
                    userId
            );
            return mapRole(role);
        } catch (EmptyResultDataAccessException exception) {
            return Role.VIEWER;
        }
    }

    private Role mapRole(String role) {
        return role != null && Role.LIBRARY_ADMIN.name().equals(role.trim().toUpperCase(Locale.ROOT))
                ? Role.LIBRARY_ADMIN
                : Role.VIEWER;
    }
}
