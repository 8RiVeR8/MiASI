package com.project.youtlix.authentication.infrastructure.out.supabase;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.time.Instant;

/**
 * Contract of the external Supabase Auth API.
 */
public interface SupabaseAuthApi {

    /** Delegates user registration to Supabase Auth. */
    SupabaseSession signUp(String email, String password);

    /** Delegates password login to Supabase Auth. */
    SupabaseSession signInWithPassword(String email, String password);

    /** Delegates logout to Supabase Auth. */
    void signOut(String jwt);

    /** Delegates password reset mail to Supabase Auth. */
    void resetPasswordForEmail(String email);

    /** Delegates password update after reset-token verification to Supabase Auth. */
    void updateUser(String jwt, String newPassword);

    /** Gets a Supabase user for a JWT. */
    SupabaseUser getUser(String jwt);

    /** Verifies a JWT using Supabase Auth. */
    boolean verify(String jwt);
}

/**
 * Technical REST implementation of the external API contract.
 */
@Component
final class SupabaseAuthRestClient implements SupabaseAuthApi {

    private final RestClient restClient;
    private final boolean configured;

    SupabaseAuthRestClient(
            RestClient.Builder restClientBuilder,
            @Value("${SUPABASE_URL:}") String supabaseUrl,
            @Value("${SUPABASE_ANON_KEY:}") String anonKey
    ) {
        this.configured = !supabaseUrl.isBlank() && !anonKey.isBlank();
        this.restClient = configured
                ? restClientBuilder.baseUrl(supabaseUrl).defaultHeader("apikey", anonKey).build()
                : restClientBuilder.build();
    }

    @Override
    public SupabaseSession signUp(String email, String password) {
        return postAuthResponse("/auth/v1/signup", new Credentials(email, password)).toSession(true);
    }

    @Override
    public SupabaseSession signInWithPassword(String email, String password) {
        return postAuthResponse("/auth/v1/token?grant_type=password", new Credentials(email, password)).toSession(false);
    }

    @Override
    public void signOut(String jwt) {
        requireConfigured();
        restClient.post()
                .uri("/auth/v1/logout")
                .headers(headers -> headers.setBearerAuth(jwt))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void resetPasswordForEmail(String email) {
        requireConfigured();
        restClient.post()
                .uri("/auth/v1/recover")
                .body(new ResetEmail(email))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public void updateUser(String jwt, String newPassword) {
        requireConfigured();
        restClient.put()
                .uri("/auth/v1/user")
                .headers(headers -> headers.setBearerAuth(jwt))
                .body(new PasswordUpdate(newPassword))
                .retrieve()
                .toBodilessEntity();
    }

    @Override
    public SupabaseUser getUser(String jwt) {
        requireConfigured();
        return restClient.get()
                .uri("/auth/v1/user")
                .headers(headers -> headers.setBearerAuth(jwt))
                .retrieve()
                .body(SupabaseUser.class);
    }

    @Override
    public boolean verify(String jwt) {
        try {
            return getUser(jwt) != null;
        } catch (RestClientResponseException exception) {
            return false;
        }
    }

    private AuthResponse postAuthResponse(String uri, Credentials credentials) {
        requireConfigured();
        AuthResponse response = restClient.post()
                .uri(uri)
                .body(credentials)
                .retrieve()
                .body(AuthResponse.class);
        if (response == null) {
            throw new IllegalStateException("Supabase Auth returned an empty session");
        }
        return response;
    }

    private void requireConfigured() {
        if (!configured) {
            throw new IllegalStateException("SUPABASE_URL and SUPABASE_ANON_KEY must be configured");
        }
    }

    private record Credentials(String email, String password) {
    }

    private record ResetEmail(String email) {
    }

    private record PasswordUpdate(String password) {
    }

    private record AuthResponse(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_at") Long expiresAt,
            @JsonProperty("expires_in") Long expiresIn,
            @JsonProperty("session") SessionPayload session
    ) {
        private SupabaseSession toSession(boolean allowPendingConfirmation) {
            String token = firstNonBlank(
                    session != null ? session.accessToken() : null,
                    accessToken
            );
            String refresh = firstNonBlank(
                    session != null ? session.refreshToken() : null,
                    refreshToken
            );
            Long expiry = firstNonNull(
                    session != null ? session.expiresAt() : null,
                    expiresAt
            );
            Long expiryInSeconds = firstNonNull(
                    session != null ? session.expiresIn() : null,
                    expiresIn
            );

            if (token == null || token.isBlank()) {
                if (allowPendingConfirmation) {
                    throw new PendingEmailConfirmationException();
                }
                throw new IllegalStateException("Supabase Auth returned an empty session");
            }
            if (expiry == null && expiryInSeconds != null) {
                expiry = Instant.now().getEpochSecond() + expiryInSeconds;
            }
            if (expiry == null) {
                throw new IllegalStateException("Supabase Auth returned a session without expiry");
            }
            return new SupabaseSession(token, refresh != null ? refresh : "", expiry);
        }
    }

    private record SessionPayload(
            @JsonProperty("access_token") String accessToken,
            @JsonProperty("refresh_token") String refreshToken,
            @JsonProperty("expires_at") Long expiresAt,
            @JsonProperty("expires_in") Long expiresIn
    ) {
    }

    private static String firstNonBlank(String first, String second) {
        if (first != null && !first.isBlank()) {
            return first;
        }
        if (second != null && !second.isBlank()) {
            return second;
        }
        return null;
    }

    private static Long firstNonNull(Long first, Long second) {
        return first != null ? first : second;
    }
}
