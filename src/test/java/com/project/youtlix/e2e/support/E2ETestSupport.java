package com.project.youtlix.e2e.support;

import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Base contract for HTTP end-to-end scenarios.
 */
public abstract class E2ETestSupport {

    @LocalServerPort
    private int port;

    protected RestTestClient webClient() {
        return RestTestClient.bindToServer()
                .baseUrl("http://localhost:" + port + "/youtlix/app")
                .build();
    }
}
