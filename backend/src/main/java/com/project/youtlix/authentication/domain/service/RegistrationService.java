package com.project.youtlix.authentication.domain.service;

import com.project.youtlix.authentication.domain.model.Email;
import com.project.youtlix.authentication.domain.model.ViewerAccount;

/** Domain service coordinating account creation rules for PU1. */
public class RegistrationService {
    private final ViewerAccountFactory factory;
    public RegistrationService(ViewerAccountFactory factory) { this.factory = factory; }
    /** Registers a viewer account using the account factory. */
    public ViewerAccount register(Email email, String rawPassword) { return factory.register(email, rawPassword); }
}
