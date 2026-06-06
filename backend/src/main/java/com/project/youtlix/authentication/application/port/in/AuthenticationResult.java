package com.project.youtlix.authentication.application.port.in;

import java.util.UUID;

/** Result returned after successful registration or login. */
public record AuthenticationResult(UUID viewerId, UUID sessionId, String token, Role role) {}
