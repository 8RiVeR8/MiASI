package com.project.youtlix.authentication.application.port.in;

/** Command for PU1 viewer registration. */
public record RegisterViewerCommand(String email, String rawPassword) {}
