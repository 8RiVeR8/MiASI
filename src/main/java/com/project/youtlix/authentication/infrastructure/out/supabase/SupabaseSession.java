package com.project.youtlix.authentication.infrastructure.out.supabase;

/**
 * Published session language returned by Supabase Auth.
 *
 * @param accessToken access JWT
 * @param refreshToken refresh token managed by Supabase
 * @param expiresAt epoch seconds of access token expiry
 */
public record SupabaseSession(String accessToken, String refreshToken, long expiresAt) {
}
