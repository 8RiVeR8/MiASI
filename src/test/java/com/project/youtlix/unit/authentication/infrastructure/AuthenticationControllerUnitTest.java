package com.project.youtlix.unit.authentication.infrastructure;

import com.project.youtlix.testsupport.fixture.FixedIdentityProvider;
import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.authentication.infrastructure.in.web.AuthenticationController;
import com.project.youtlix.authentication.infrastructure.out.supabase.InvalidCredentialsException;
import com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseAuthApi;
import com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseSession;
import com.project.youtlix.testsupport.annotation.UnitTest;
import com.project.youtlix.testsupport.fixture.ViewerTestAccount;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@UnitTest
class AuthenticationControllerUnitTest {

    @Test
    void loginReturnsMappedAuthenticationResponse() {
        UUID viewerId = ViewerTestAccount.VIEWER_ID;
        AuthenticationController controller = new AuthenticationController(
                new FakeSupabaseAuthApi("access-token", "refresh-token", 9999L),
                new FixedIdentityProvider(ViewerTestAccount.viewerIdentity())
        );

        AuthenticationController.AuthenticationResponse response = controller.login(
                new AuthenticationController.CredentialsRequest(ViewerTestAccount.EMAIL, "secret")
        );

        assertThat(response.accessToken()).isEqualTo("access-token");
        assertThat(response.refreshToken()).isEqualTo("refresh-token");
        assertThat(response.viewerId()).isEqualTo(viewerId);
        assertThat(response.role()).isEqualTo("VIEWER");
    }

    @Test
    void invalidCredentialsReturnUnauthorized() {
        AuthenticationController controller = new AuthenticationController(
                new FakeSupabaseAuthApi(null, null, 0L) {
                    @Override
                    public SupabaseSession signInWithPassword(String email, String password) {
                        throw new InvalidCredentialsException();
                    }
                },
                new FixedIdentityProvider(ViewerTestAccount.viewerIdentity())
        );

        ResponseEntity<Map<String, String>> response = controller.handleInvalidCredentials(new InvalidCredentialsException());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    private static class FakeSupabaseAuthApi implements SupabaseAuthApi {
        private final String accessToken;
        private final String refreshToken;
        private final long expiresAt;

        FakeSupabaseAuthApi(String accessToken, String refreshToken, long expiresAt) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
            this.expiresAt = expiresAt;
        }

        @Override
        public SupabaseSession signUp(String email, String password) {
            return new SupabaseSession(accessToken, refreshToken, expiresAt);
        }

        @Override
        public SupabaseSession signInWithPassword(String email, String password) {
            return new SupabaseSession(accessToken, refreshToken, expiresAt);
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
        public com.project.youtlix.authentication.infrastructure.out.supabase.SupabaseUser getUser(String jwt) {
            return null;
        }

        @Override
        public boolean verify(String jwt) {
            return true;
        }
    }
}
