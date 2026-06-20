package com.project.youtlix.unit.authentication.infrastructure;

import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseAuthApi;
import com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseIdentityAdapter;
import com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseUser;
import com.project.youtlix.testsupport.annotation.UnitTest;
import org.junit.jupiter.api.Test;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@UnitTest
class SupabaseIdentityAdapterUnitTest {

    @Test
    void mapsSupabaseUserToInternalIdentityUsingProfileRole() {
        UUID userId = UUID.randomUUID();
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(
                eq("SELECT role::text FROM identity.user_profiles WHERE id = ?"),
                eq(String.class),
                eq(userId)
        )).thenReturn("LIBRARY_ADMIN");

        SupabaseIdentityAdapter adapter = new SupabaseIdentityAdapter(new FakeSupabaseAuthApi(userId), jdbcTemplate);

        UserIdentity identity = adapter.currentIdentity("jwt");

        assertThat(identity.viewerId().value()).isEqualTo(userId);
        assertThat(identity.role()).isEqualTo(Role.LIBRARY_ADMIN);
        assertThat(adapter.verify("jwt")).isTrue();
    }

    @Test
    void defaultsToViewerWhenProfileMissing() {
        UUID userId = UUID.randomUUID();
        JdbcTemplate jdbcTemplate = mock(JdbcTemplate.class);
        when(jdbcTemplate.queryForObject(
                eq("SELECT role::text FROM identity.user_profiles WHERE id = ?"),
                eq(String.class),
                eq(userId)
        )).thenThrow(new EmptyResultDataAccessException(1));

        SupabaseIdentityAdapter adapter = new SupabaseIdentityAdapter(new FakeSupabaseAuthApi(userId), jdbcTemplate);

        assertThat(adapter.currentIdentity("jwt").role()).isEqualTo(Role.VIEWER);
    }

    private record FakeSupabaseAuthApi(UUID id) implements SupabaseAuthApi {
        @Override
        public com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseSession signUp(String email, String password) {
            throw new UnsupportedOperationException();
        }

        @Override
        public com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseSession signInWithPassword(String email, String password) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void signOut(String jwt) {
        }

        @Override
        public void resetPasswordForEmail(String email) {
        }

        @Override
        public void updateUser(String jwt, String newPassword) {
        }

        @Override
        public SupabaseUser getUser(String jwt) {
            return new SupabaseUser(id, "viewer@example.com", null, Map.of());
        }

        @Override
        public boolean verify(String jwt) {
            return true;
        }
    }
}
