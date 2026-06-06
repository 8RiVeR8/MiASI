package com.project.youtlix.authentication.application.port.out;

import com.project.youtlix.authentication.domain.model.Email;
import com.project.youtlix.authentication.domain.model.ViewerAccount;
import com.project.youtlix.authentication.domain.model.ViewerId;
import java.util.Optional;

/** Output port for storing and loading viewer accounts. */
public interface ViewerAccountRepository {
    void save(ViewerAccount account);
    Optional<ViewerAccount> ofId(ViewerId id);
    Optional<ViewerAccount> ofEmail(Email email);
    boolean existsByEmail(Email email);
}
