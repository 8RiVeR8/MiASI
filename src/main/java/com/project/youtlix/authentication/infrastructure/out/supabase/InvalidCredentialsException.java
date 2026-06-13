package com.project.youtlix.authentication.infrastructure.out.supabase;

/**
 * Raised when Supabase rejects login credentials.
 */
public class InvalidCredentialsException extends RuntimeException {

    public InvalidCredentialsException() {
        super("Invalid email or password");
    }
}
