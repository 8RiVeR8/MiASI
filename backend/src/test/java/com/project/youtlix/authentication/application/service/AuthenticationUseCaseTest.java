package com.project.youtlix.authentication.application.service;

import com.project.youtlix.authentication.application.port.in.*;
import com.project.youtlix.authentication.application.port.out.EmailSender;
import com.project.youtlix.authentication.domain.model.SessionToken;
import com.project.youtlix.authentication.domain.service.*;
import com.project.youtlix.authentication.infrastructure.out.persistence.*;
import com.project.youtlix.common.infrastructure.event.InMemoryDomainEventBus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Application tests for PU1-PU4 authentication flows. */
class AuthenticationUseCaseTest {
    @Test
    void registerCreatesViewerSessionAndPublishedIdentity() {
        InMemoryDomainEventBus events = new InMemoryDomainEventBus();
        EmailSender emailSender = (to, token) -> { };
        AuthenticationApplicationService service = new AuthenticationApplicationService(
                new InMemoryViewerAccountRepository(), new InMemorySessionRepository(), new InMemoryResetLinkRepository(),
                emailSender, events, new RegistrationService(new ViewerAccountFactory()), new AuthenticationService(), new PasswordResetService());

        AuthenticationResult result = service.register(new RegisterViewerCommand("viewer@example.com", "secret"));

        assertEquals(Role.VIEWER, result.role());
        assertTrue(service.verify(new SessionToken(result.token())));
        assertFalse(events.publishedEvents().isEmpty());
    }
}
