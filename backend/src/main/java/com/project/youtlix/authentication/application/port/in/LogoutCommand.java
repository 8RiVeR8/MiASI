package com.project.youtlix.authentication.application.port.in;

import java.util.UUID;

/** Command for PU3 logout. */
public record LogoutCommand(UUID sessionId) {}
