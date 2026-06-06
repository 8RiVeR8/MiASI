package com.project.youtlix.authentication.infrastructure.out.email;

import com.project.youtlix.authentication.application.port.out.EmailSender;
import com.project.youtlix.authentication.domain.model.Email;
import com.project.youtlix.authentication.domain.model.ResetToken;
import org.springframework.stereotype.Component;

/** Placeholder SMTP adapter for PU4 password reset emails. */
@Component
public class SmtpEmailAdapter implements EmailSender {
    @Override public void sendResetLink(Email to, ResetToken token) {
        // Intentionally empty until SMTP infrastructure is configured.
    }
}
