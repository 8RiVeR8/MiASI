package com.project.youtlix.authentication.infrastructure.out.persistence;

import com.project.youtlix.authentication.application.port.out.ResetLinkRepository;
import com.project.youtlix.authentication.domain.model.PasswordResetLink;
import com.project.youtlix.authentication.domain.model.ResetToken;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary in-memory adapter for reset links until persistence is implemented. */
@Repository
public class InMemoryResetLinkRepository implements ResetLinkRepository {
    private final Map<ResetToken, PasswordResetLink> links = new ConcurrentHashMap<>();
    @Override public void save(PasswordResetLink link) { links.put(link.token(), link); }
    @Override public Optional<PasswordResetLink> ofToken(ResetToken token) { return Optional.ofNullable(links.get(token)); }
}
