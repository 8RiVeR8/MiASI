package com.project.youtlix.authentication.application.port.in;

import com.project.youtlix.authentication.domain.model.SessionToken;
import java.util.Optional;

/** Open Host Service / Published Language for identity verification. */
public interface IdentityProvider {
    /** Authenticates a session token and returns published identity data. */
    Optional<UserIdentity> authenticate(SessionToken token);
    /** Verifies whether a session token is valid. */
    boolean verify(SessionToken token);
}
