package com.project.youtlix.authentication.infrastructure.out.supabase;

import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class SupabaseIdentityAdapterTest {

    @Test
    void mapsSupabaseUserToInternalIdentity() {
        UUID userId = UUID.randomUUID();
        SupabaseIdentityAdapter adapter = new SupabaseIdentityAdapter(new FakeSupabaseAuthApi(userId, "LIBRARY_ADMIN"));

        UserIdentity identity = adapter.currentIdentity("jwt");

        assertThat(identity.viewerId().value()).isEqualTo(userId);
        assertThat(identity.role()).isEqualTo(Role.LIBRARY_ADMIN);
        assertThat(adapter.verify("jwt")).isTrue();
    }

    private record FakeSupabaseAuthApi(UUID id, String role) implements SupabaseAuthApi {
        @Override
        public SupabaseSession signUp(String email, String password) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SupabaseSession signInWithPassword(String email, String password) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void signOut(String jwt) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void resetPasswordForEmail(String email) {
            throw new UnsupportedOperationException();
        }

        @Override
        public SupabaseUser getUser(String jwt) {
            return new SupabaseUser(id, "viewer@example.com", role, Map.of());
        }

        @Override
        public boolean verify(String jwt) {
            return true;
        }
    }
}
