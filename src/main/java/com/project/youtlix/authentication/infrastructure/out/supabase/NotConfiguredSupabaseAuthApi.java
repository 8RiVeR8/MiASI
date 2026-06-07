package com.project.youtlix.authentication.infrastructure.out.supabase;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

/**
 * Placeholder Supabase client used until the HTTP client is configured.
 *
 * <p>It lets the application start with a clear failure at the integration
 * boundary instead of hiding the fact that Supabase credentials are missing.</p>
 */
@Component
@ConditionalOnMissingBean(SupabaseAuthApi.class)
public class NotConfiguredSupabaseAuthApi implements SupabaseAuthApi {

    @Override
    public SupabaseSession signUp(String email, String password) {
        throw notConfigured();
    }

    @Override
    public SupabaseSession signInWithPassword(String email, String password) {
        throw notConfigured();
    }

    @Override
    public void signOut(String jwt) {
        throw notConfigured();
    }

    @Override
    public void resetPasswordForEmail(String email) {
        throw notConfigured();
    }

    @Override
    public SupabaseUser getUser(String jwt) {
        throw notConfigured();
    }

    @Override
    public boolean verify(String jwt) {
        throw notConfigured();
    }

    private IllegalStateException notConfigured() {
        return new IllegalStateException("Supabase Auth API client is not configured yet");
    }
}
