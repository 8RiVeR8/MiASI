package com.project.youtlix.authentication.application.port.out;

import com.project.youtlix.authentication.domain.model.UserIdentity;

/**
 * Port that exposes current user identity to application modules.
 */
public interface IdentityProvider {

    /**
     * Reads the current identity from a Supabase JWT.
     *
     * @param jwt bearer token from Supabase Auth
     * @return internal identity independent from Supabase API classes
     */
    UserIdentity currentIdentity(String jwt);

    /**
     * Verifies a Supabase JWT without leaking Supabase objects to callers.
     *
     * @param jwt bearer token from Supabase Auth
     * @return true if token is valid
     */
    boolean verify(String jwt);
}
