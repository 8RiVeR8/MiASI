package com.project.youtlix.authentication.domain.model;

import com.project.youtlix.authentication.domain.model.event.PasswordReset;
import com.project.youtlix.authentication.domain.model.event.ViewerRegistered;
import com.project.youtlix.common.domain.DomainEvent;
import com.project.youtlix.common.domain.DomainException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

/** Aggregate root representing a viewer account in the authentication context. */
public class ViewerAccount {
    private final ViewerId id;
    private final Email email;
    private Password password;
    private AccountStatus status;
    private final List<DomainEvent> events = new ArrayList<>();

    public ViewerAccount(ViewerId id, Email email, Password password, AccountStatus status) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.status = status;
        events.add(new ViewerRegistered(id, email, Instant.now()));
    }

    /** Changes the password and records a domain event. */
    public void changePassword(Password newPassword) {
        if (status == AccountStatus.LOCKED) throw new DomainException("Locked account cannot change password");
        this.password = newPassword;
        events.add(new PasswordReset(id, Instant.now()));
    }

    /** Activates the account. */
    public void activate() { this.status = AccountStatus.ACTIVE; }

    /** Checks if provided credentials match this account. */
    public boolean matches(Credentials credentials) {
        return status == AccountStatus.ACTIVE && email.equals(credentials.email()) && password.verify(credentials.rawPassword());
    }

    /** Returns domain events recorded by this aggregate. */
    public List<DomainEvent> occurredEvents() { return List.copyOf(events); }
    public ViewerId id() { return id; }
    public Email email() { return email; }
    public AccountStatus status() { return status; }
}
