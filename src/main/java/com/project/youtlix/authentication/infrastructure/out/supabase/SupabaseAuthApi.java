package com.project.youtlix.authentication.infrastructure.out.supabase;

/**
 * External Supabase Auth API boundary.
 *
 * <p>Registration, login, logout and password reset are delegated to Supabase.
 * The application keeps this interface only to isolate provider details.</p>
 */
public interface SupabaseAuthApi {

    /**
     * Delegates user registration to Supabase Auth.
     */
    SupabaseSession signUp(String email, String password);

    /**
     * Delegates password login to Supabase Auth.
     */
    SupabaseSession signInWithPassword(String email, String password);

    /**
     * Delegates logout to Supabase Auth.
     */
    void signOut(String jwt);

    /**
     * Delegates password reset mail to Supabase Auth.
     */
    void resetPasswordForEmail(String email);

    /**
     * Gets a Supabase user for a JWT.
     */
    SupabaseUser getUser(String jwt);

    /**
     * Verifies a JWT using Supabase Auth.
     */
    boolean verify(String jwt);
}
