package com.project.youtlix.e2e.scenario;

import com.project.youtlix.e2e.support.E2ETestSupport;
import com.project.youtlix.testsupport.annotation.E2ETest;
import com.project.youtlix.testsupport.fixture.AdminTestAccount;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@E2ETest
class AdminContentLibraryCycleE2ETest extends E2ETestSupport {

    private String adminToken;
    private UUID createdContentId;

    @AfterEach
    void cleanup() {
        if (adminToken != null && createdContentId != null) {
            webClient().delete()
                    .uri("/admin/content/{id}", createdContentId)
                    .header("Authorization", "Bearer " + adminToken)
                    .exchange();
        }
    }

    @Test
    void adminCreatesMovieAndCanSearchIt() {
        AdminTestAccount.assumeConfigured();
        adminToken = login();
        String marker = "e2e-" + UUID.randomUUID();

        String createResponse = webClient().post()
                .uri("/admin/content")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {
                          "type": "MOVIE",
                          "title": "%s",
                          "description": "E2E cycle",
                          "thumbnailUrl": "thumb",
                          "genre": "DOCUMENTARY",
                          "releaseYear": 2026,
                          "keywords": ["e2e"],
                          "durationSeconds": 600,
                          "videoUri": "cdn://e2e",
                          "languages": ["pl"]
                        }
                        """.formatted(marker))
                .exchange()
                .expectStatus().isCreated()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        createdContentId = UUID.fromString(createResponse.replace("\"", "").trim());
        assertThat(createdContentId).isNotNull();

        String searchResponse = webClient().get()
                .uri("/library/search?phrase={phrase}", marker)
                .header("Authorization", "Bearer " + adminToken)
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        assertThat(searchResponse).contains(marker);
    }

    private String login() {
        String body = webClient().post()
                .uri("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .body("""
                        {"email":"%s","password":"%s"}
                        """.formatted(
                        AdminTestAccount.email().orElseThrow(),
                        AdminTestAccount.password().orElseThrow()
                ))
                .exchange()
                .expectStatus().isOk()
                .expectBody(String.class)
                .returnResult()
                .getResponseBody();

        int tokenStart = body.indexOf("\"accessToken\":\"") + 15;
        int tokenEnd = body.indexOf('"', tokenStart);
        return body.substring(tokenStart, tokenEnd);
    }
}
