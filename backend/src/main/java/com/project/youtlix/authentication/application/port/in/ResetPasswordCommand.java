package com.project.youtlix.authentication.application.port.in;

/** Command for completing PU4 password reset. */
public record ResetPasswordCommand(String token, String newPassword) {}
