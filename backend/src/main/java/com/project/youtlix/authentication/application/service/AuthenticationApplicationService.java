package com.project.youtlix.authentication.application.service;

import com.project.youtlix.authentication.application.port.in.*;
import com.project.youtlix.authentication.application.port.out.*;
import com.project.youtlix.authentication.domain.model.*;
import com.project.youtlix.authentication.domain.service.AuthenticationService;
import com.project.youtlix.authentication.domain.service.PasswordResetService;
import com.project.youtlix.authentication.domain.service.RegistrationService;
import com.project.youtlix.common.application.port.out.DomainEventPublisher;
import com.project.youtlix.common.domain.DomainException;
import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

/** Application service implementing authentication use cases PU1-PU4. */
@Service
public class AuthenticationApplicationService implements AuthenticationUseCase, IdentityProvider {
    private final ViewerAccountRepository accounts;
    private final SessionRepository sessions;
    private final ResetLinkRepository resetLinks;
    private final EmailSender emailSender;
    private final DomainEventPublisher eventPublisher;
    private final RegistrationService registrationService;
    private final AuthenticationService authenticationService;
    private final PasswordResetService passwordResetService;

    public AuthenticationApplicationService(ViewerAccountRepository accounts, SessionRepository sessions,
            ResetLinkRepository resetLinks, EmailSender emailSender, DomainEventPublisher eventPublisher,
            RegistrationService registrationService, AuthenticationService authenticationService,
            PasswordResetService passwordResetService) {
        this.accounts = accounts;
        this.sessions = sessions;
        this.resetLinks = resetLinks;
        this.emailSender = emailSender;
        this.eventPublisher = eventPublisher;
        this.registrationService = registrationService;
        this.authenticationService = authenticationService;
        this.passwordResetService = passwordResetService;
    }

    @Override public AuthenticationResult register(RegisterViewerCommand command) {
        Email email = new Email(command.email());
        if (accounts.existsByEmail(email)) throw new DomainException("Email is already registered");
        ViewerAccount account = registrationService.register(email, command.rawPassword());
        accounts.save(account);
        Session session = authenticationService.login(account, new Credentials(email, command.rawPassword()));
        sessions.save(session);
        eventPublisher.publishAll(account.occurredEvents());
        eventPublisher.publishAll(session.occurredEvents());
        return result(account, session);
    }

    @Override public AuthenticationResult login(LoginCommand command) {
        Email email = new Email(command.email());
        ViewerAccount account = accounts.ofEmail(email).orElseThrow(() -> new DomainException("Account not found"));
        Session session = authenticationService.login(account, new Credentials(email, command.rawPassword()));
        sessions.save(session);
        eventPublisher.publishAll(session.occurredEvents());
        return result(account, session);
    }

    @Override public void logout(LogoutCommand command) {
        SessionId sessionId = new SessionId(command.sessionId());
        Session session = sessions.ofId(sessionId).orElseThrow(() -> new DomainException("Session not found"));
        authenticationService.logout(session);
        sessions.remove(sessionId);
        eventPublisher.publishAll(session.occurredEvents());
    }

    @Override public void requestReset(RequestPasswordResetCommand command) {
        ViewerAccount account = accounts.ofEmail(new Email(command.email())).orElseThrow(() -> new DomainException("Account not found"));
        PasswordResetLink link = passwordResetService.requestReset(account);
        resetLinks.save(link);
        emailSender.sendResetLink(account.email(), link.token());
    }

    @Override public void resetPassword(ResetPasswordCommand command) {
        PasswordResetLink link = resetLinks.ofToken(new ResetToken(command.token())).orElseThrow(() -> new DomainException("Reset link not found"));
        ViewerAccount account = accounts.ofId(link.viewerId()).orElseThrow(() -> new DomainException("Account not found"));
        passwordResetService.resetPassword(link, account, Password.fromRaw(command.newPassword()), Instant.now());
        resetLinks.save(link);
        accounts.save(account);
        eventPublisher.publishAll(account.occurredEvents());
    }

    @Override public Optional<UserIdentity> authenticate(SessionToken token) {
        return sessionIdFromToken(token).flatMap(sessions::ofId)
                .filter(session -> session.isValid(Instant.now()))
                .map(session -> new UserIdentity(session.viewerId().value(), Role.VIEWER));
    }

    @Override public boolean verify(SessionToken token) { return authenticate(token).isPresent(); }

    private AuthenticationResult result(ViewerAccount account, Session session) {
        return new AuthenticationResult(account.id().value(), session.id().value(), session.token().value(), Role.VIEWER);
    }

    private Optional<SessionId> sessionIdFromToken(SessionToken token) {
        try { return Optional.of(new SessionId(UUID.fromString(token.value()))); }
        catch (IllegalArgumentException exception) { return Optional.empty(); }
    }
}
