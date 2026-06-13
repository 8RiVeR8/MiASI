package com.project.youtlix.authentication.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.out.IdentityProvider;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseAuthApi;
import com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseSession;
import com.project.youtlix.authentication.infrastructure.out.supabase.PendingEmailConfirmationException;
import com.project.youtlix.common.infrastructure.in.web.OpenApiConfig;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

import java.util.UUID;

/**
 * Web API adapter for Supabase Auth use cases PU1-PU4.
 */
@RestController
public class AuthenticationController {

    private final SupabaseAuthApi supabaseAuthApi;
    private final IdentityProvider identityProvider;

    /** Creates authentication web adapter. */
    public AuthenticationController(SupabaseAuthApi supabaseAuthApi, IdentityProvider identityProvider) {
        this.supabaseAuthApi = supabaseAuthApi;
        this.identityProvider = identityProvider;
    }

    /** Handles PU1 registration through Supabase Auth. */
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public AuthenticationResponse register(@RequestBody CredentialsRequest request) {
        SupabaseSession session = supabaseAuthApi.signUp(request.email(), request.password());
        UserIdentity identity = identityProvider.currentIdentity(session.accessToken());
        return AuthenticationResponse.from(session, identity);
    }

    /** Handles PU2 login through Supabase Auth. */
    @PostMapping("/login")
    public AuthenticationResponse login(@RequestBody CredentialsRequest request) {
        SupabaseSession session = supabaseAuthApi.signInWithPassword(request.email(), request.password());
        UserIdentity identity = identityProvider.currentIdentity(session.accessToken());
        return AuthenticationResponse.from(session, identity);
    }

    /** Handles PU3 logout through Supabase Auth. */
    @PostMapping("/logout")
    @SecurityRequirement(name = OpenApiConfig.BEARER_AUTH)
    public void logout(@RequestHeader("Authorization") String authorization) {
        supabaseAuthApi.signOut(bearerToken(authorization));
    }

    /** Handles PU4 reset e-mail request through Supabase Auth. */
    @PostMapping("/reset-request")
    public void resetRequest(@RequestBody ResetRequest request) {
        supabaseAuthApi.resetPasswordForEmail(request.email());
    }

    /** Handles PU4 password update after Supabase reset token verification. */
    @PostMapping("/reset-password")
    public void resetPassword(@RequestBody ResetPasswordRequest request) {
        supabaseAuthApi.updateUser(request.token(), request.newPassword());
    }

    @ExceptionHandler(PendingEmailConfirmationException.class)
    public ResponseEntity<Map<String, String>> handlePendingEmailConfirmation(PendingEmailConfirmationException exception) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", exception.getMessage()));
    }

    private String bearerToken(String authorization) {
        if (authorization == null || authorization.isBlank()) {
            throw new IllegalArgumentException("Authorization header is required");
        }
        return authorization.startsWith("Bearer ") ? authorization.substring("Bearer ".length()) : authorization;
    }

    public record CredentialsRequest(String email, String password) {
    }

    public record ResetRequest(String email) {
    }

    public record ResetPasswordRequest(String token, String newPassword) {
    }

    public record AuthenticationResponse(
            String accessToken,
            String refreshToken,
            long expiresAt,
            UUID viewerId,
            String role
    ) {
        static AuthenticationResponse from(SupabaseSession session, UserIdentity identity) {
            return new AuthenticationResponse(
                    session.accessToken(),
                    session.refreshToken(),
                    session.expiresAt(),
                    identity.viewerId().value(),
                    identity.role().name()
            );
        }
    }
}
