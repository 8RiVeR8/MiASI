package com.project.youtlix.authentication.domain.service;

import com.project.youtlix.authentication.domain.model.Credentials;
import com.project.youtlix.authentication.domain.model.Session;
import com.project.youtlix.authentication.domain.model.ViewerAccount;
import com.project.youtlix.common.domain.DomainException;
import java.time.Duration;
import java.time.Instant;

/** Domain service responsible for login and logout decisions in PU2-PU3. */
public class AuthenticationService {
    /** Creates a session if credentials match the viewer account. */
    public Session login(ViewerAccount account, Credentials credentials) {
        if (!account.matches(credentials)) throw new DomainException("Invalid credentials");
        return Session.create(account.id(), Instant.now().plus(Duration.ofHours(8)));
    }
    /** Invalidates an existing session. */
    public void logout(Session session) { session.invalidate(); }
}
