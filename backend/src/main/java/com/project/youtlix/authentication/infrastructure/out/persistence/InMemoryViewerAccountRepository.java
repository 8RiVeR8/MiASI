package com.project.youtlix.authentication.infrastructure.out.persistence;

import com.project.youtlix.authentication.application.port.out.ViewerAccountRepository;
import com.project.youtlix.authentication.domain.model.Email;
import com.project.youtlix.authentication.domain.model.ViewerAccount;
import com.project.youtlix.authentication.domain.model.ViewerId;
import org.springframework.stereotype.Repository;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/** Temporary in-memory adapter for viewer accounts until persistence is implemented. */
@Repository
public class InMemoryViewerAccountRepository implements ViewerAccountRepository {
    private final Map<ViewerId, ViewerAccount> accounts = new ConcurrentHashMap<>();
    @Override public void save(ViewerAccount account) { accounts.put(account.id(), account); }
    @Override public Optional<ViewerAccount> ofId(ViewerId id) { return Optional.ofNullable(accounts.get(id)); }
    @Override public Optional<ViewerAccount> ofEmail(Email email) { return accounts.values().stream().filter(a -> a.email().equals(email)).findFirst(); }
    @Override public boolean existsByEmail(Email email) { return ofEmail(email).isPresent(); }
}
