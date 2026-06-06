package com.project.youtlix.authentication.application.port.out;

import com.project.youtlix.authentication.domain.model.Email;
import com.project.youtlix.authentication.domain.model.ResetToken;

/** Output port for sending reset links to viewers. */
public interface EmailSender {
    void sendResetLink(Email to, ResetToken token);
}
