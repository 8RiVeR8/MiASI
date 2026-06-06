package com.project.youtlix.authentication.infrastructure.out.persistence;

import com.project.youtlix.authentication.application.port.out.SessionRepository;
import com.project.youtlix.authentication.domain.model.Session;
import com.project.youtlix.authentication.domain.model.SessionId;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary in-memory adapter for sessions until persistence is implemented. */
@Repository
public class InMemorySessionRepository implements SessionRepository {
    private final Map<SessionId, Session> sessions = new ConcurrentHashMap<>();
    @Override public void save(Session session) { sessions.put(session.id(), session); }
    @Override public Optional<Session> ofId(SessionId id) { return Optional.ofNullable(sessions.get(id)); }
    @Override public void remove(SessionId id) { sessions.remove(id); }
}
