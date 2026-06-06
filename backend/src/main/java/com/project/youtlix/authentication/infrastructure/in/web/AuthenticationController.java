package com.project.youtlix.authentication.infrastructure.in.web;

import com.project.youtlix.authentication.application.port.in.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.UUID;

/** Driving web adapter exposing authentication endpoints from PU1-PU4. */
@RestController
@RequestMapping("/auth")
public class AuthenticationController {
    private final AuthenticationUseCase authentication;
    public AuthenticationController(AuthenticationUseCase authentication) { this.authentication = authentication; }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResult> register(@RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authentication.register(new RegisterViewerCommand(request.email(), request.password())));
    }

    @PostMapping("/login")
    public AuthenticationResult login(@RequestBody LoginRequest request) {
        return authentication.login(new LoginCommand(request.email(), request.password()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody LogoutRequest request) {
        authentication.logout(new LogoutCommand(request.sessionId()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-request")
    public ResponseEntity<Void> requestReset(@RequestBody ResetRequest request) {
        authentication.requestReset(new RequestPasswordResetCommand(request.email()));
        return ResponseEntity.ok().build();
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(@RequestBody ResetPasswordRequest request) {
        authentication.resetPassword(new ResetPasswordCommand(request.token(), request.newPassword()));
        return ResponseEntity.ok().build();
    }

    public record RegisterRequest(String email, String password) {}
    public record LoginRequest(String email, String password) {}
    public record LogoutRequest(UUID sessionId) {}
    public record ResetRequest(String email) {}
    public record ResetPasswordRequest(String token, String newPassword) {}
}
