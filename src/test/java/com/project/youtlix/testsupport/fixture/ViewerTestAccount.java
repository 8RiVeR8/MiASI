package com.project.youtlix.testsupport.fixture;

import com.project.youtlix.authentication.domain.model.Role;
import com.project.youtlix.authentication.domain.model.UserIdentity;
import com.project.youtlix.authentication.domain.model.ViewerId;

import java.util.UUID;

/**
 * Identity fixture for the default viewer test account.
 */
public final class ViewerTestAccount {

    public static final String EMAIL = "bocianek@example.com";
    public static final UUID VIEWER_ID = UUID.fromString("f4e6c1e7-7280-4924-959f-e84957bbf1ea");
    public static final String BEARER = "Bearer test-jwt";

    private ViewerTestAccount() {
    }

    public static ViewerId authViewerId() {
        return new ViewerId(VIEWER_ID);
    }

    public static com.project.youtlix.recommendation.domain.model.ViewerId recommendationViewerId() {
        return new com.project.youtlix.recommendation.domain.model.ViewerId(VIEWER_ID);
    }

    public static UserIdentity viewerIdentity() {
        return new UserIdentity(authViewerId(), Role.VIEWER);
    }
}
