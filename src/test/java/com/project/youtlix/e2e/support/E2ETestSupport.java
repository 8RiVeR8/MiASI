package com.project.youtlix.e2e.support;

import com.project.youtlix.testsupport.annotation.E2ETest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.servlet.client.RestTestClient;

/**
 * Base contract for HTTP end-to-end scenarios.
 */
@E2ETest
abstract class E2ETestSupport {

    @LocalServerPort
    private int port;

    @Autowired
    private RestTestClient.Builder webTestClientBuilder;

    protected RestTestClient webClient() {
        return webTestClientBuilder
                .baseUrl("http://localhost:" + port + "/youtlix/app")
                .build();
    }
}
