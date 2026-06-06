package com.project.youtlix.authentication.application.port.in;

/** Command for requesting a reset link in PU4. */
public record RequestPasswordResetCommand(String email) {}
