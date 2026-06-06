package com.project.youtlix.authentication.application.port.out;

import com.project.youtlix.authentication.domain.model.Session;
import com.project.youtlix.authentication.domain.model.SessionId;
import java.util.Optional;

/** Output port for storing authenticated sessions. */
public interface SessionRepository {
    void save(Session session);
    Optional<Session> ofId(SessionId id);
    void remove(SessionId id);
}
