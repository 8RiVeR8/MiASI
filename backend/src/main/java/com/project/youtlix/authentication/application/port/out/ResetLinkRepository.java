package com.project.youtlix.authentication.application.port.out;

import com.project.youtlix.authentication.domain.model.PasswordResetLink;
import com.project.youtlix.authentication.domain.model.ResetToken;
import java.util.Optional;

/** Output port for password reset links. */
public interface ResetLinkRepository {
    void save(PasswordResetLink link);
    Optional<PasswordResetLink> ofToken(ResetToken token);
}
