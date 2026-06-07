package com.project.youtlix.authentication.infrastructure.out.supabase;

import java.util.Map;
import java.util.UUID;

/**
 * Published user language returned by Supabase Auth.
 *
 * @param id Supabase user UUID
 * @param email user e-mail kept by Supabase
 * @param role role name from Supabase metadata/profile
 * @param userMetadata raw metadata from Supabase
 */
public record SupabaseUser(UUID id, String email, String role, Map<String, Object> userMetadata) {
}
