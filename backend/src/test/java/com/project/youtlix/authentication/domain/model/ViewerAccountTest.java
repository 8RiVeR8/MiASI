package com.project.youtlix.authentication.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/** Unit tests for the ViewerAccount aggregate from PU1-PU2. */
class ViewerAccountTest {
    @Test
    void registeredActiveAccountMatchesCorrectCredentials() {
        ViewerAccount account = new ViewerAccount(ViewerId.newId(), new Email("viewer@example.com"), Password.fromRaw("secret"), AccountStatus.ACTIVE);
        assertTrue(account.matches(new Credentials(new Email("viewer@example.com"), "secret")));
        assertFalse(account.matches(new Credentials(new Email("viewer@example.com"), "wrong")));
    }

    @Test
    void changingPasswordPublishesPasswordResetEvent() {
        ViewerAccount account = new ViewerAccount(ViewerId.newId(), new Email("viewer@example.com"), Password.fromRaw("old"), AccountStatus.ACTIVE);
        account.changePassword(Password.fromRaw("new"));
        assertEquals(2, account.occurredEvents().size());
    }
}
