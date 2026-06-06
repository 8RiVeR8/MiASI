package com.project.youtlix.authentication.domain.service;

import com.project.youtlix.authentication.domain.model.Password;
import com.project.youtlix.authentication.domain.model.PasswordResetLink;
import com.project.youtlix.authentication.domain.model.ViewerAccount;
import com.project.youtlix.common.domain.DomainException;
import java.time.Duration;
import java.time.Instant;

/** Domain service handling password reset link creation and consumption in PU4. */
public class PasswordResetService {
    /** Creates a reset link for the provided account. */
    public PasswordResetLink requestReset(ViewerAccount account) { return PasswordResetLink.create(account.id(), Instant.now().plus(Duration.ofMinutes(30))); }
    /** Consumes a reset link and changes the account password. */
    public void resetPassword(PasswordResetLink link, ViewerAccount account, Password newPassword, Instant now) {
        if (!link.isUsable(now)) throw new DomainException("Password reset link is invalid or expired");
        link.consume();
        account.changePassword(newPassword);
    }
}
