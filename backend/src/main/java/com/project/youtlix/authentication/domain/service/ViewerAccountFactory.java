package com.project.youtlix.authentication.domain.service;

import com.project.youtlix.authentication.domain.model.AccountStatus;
import com.project.youtlix.authentication.domain.model.Email;
import com.project.youtlix.authentication.domain.model.Password;
import com.project.youtlix.authentication.domain.model.ViewerAccount;
import com.project.youtlix.authentication.domain.model.ViewerId;

/** Factory responsible for creating viewer accounts during PU1 registration. */
public class ViewerAccountFactory {
    /** Creates an active viewer account from an email and raw password. */
    public ViewerAccount register(Email email, String rawPassword) {
        return new ViewerAccount(ViewerId.newId(), email, Password.fromRaw(rawPassword), AccountStatus.ACTIVE);
    }
}
