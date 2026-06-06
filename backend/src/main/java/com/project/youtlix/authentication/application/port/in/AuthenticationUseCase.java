package com.project.youtlix.authentication.application.port.in;

/** Inbound port exposing PU1-PU4 authentication use cases. */
public interface AuthenticationUseCase {
    /** Registers a new viewer and creates a session. */
    AuthenticationResult register(RegisterViewerCommand command);
    /** Logs a viewer in and creates a session. */
    AuthenticationResult login(LoginCommand command);
    /** Invalidates an existing session. */
    void logout(LogoutCommand command);
    /** Requests a password reset link. */
    void requestReset(RequestPasswordResetCommand command);
    /** Resets the password using a reset token. */
    void resetPassword(ResetPasswordCommand command);
}
