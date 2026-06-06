package com.project.youtlix.authentication.application.port.in;

/** Command for PU2 login. */
public record LoginCommand(String email, String rawPassword) {}
