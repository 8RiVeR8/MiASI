package com.project.youtlix.authentication.infrastructure.out.supabase;

/**
 * Raised when Supabase creates a user but does not issue a session yet.
 */
public class PendingEmailConfirmationException extends RuntimeException {

    public PendingEmailConfirmationException() {
        super("E-mail confirmation required before sign-in");
    }
}
