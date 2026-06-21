package com.project.youtlix.integration.contentlibrary;

import com.project.youtlix.integration.support.IntegrationTestSupport;
import com.project.youtlix.testsupport.annotation.IntegrationTest;
import com.project.youtlix.testsupport.fixture.AdminTestAccount;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@IntegrationTest
@AutoConfigureMockMvc
class AuthenticationWebMvcIntegrationTest extends IntegrationTestSupport {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginRejectsInvalidCredentials() throws Exception {
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"invalid@example.com","password":"wrong-password"}
                                """))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginAcceptsConfiguredAdminAccount() throws Exception {
        AdminTestAccount.assumeConfigured();

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"email":"%s","password":"%s"}
                                """.formatted(
                                AdminTestAccount.email().orElseThrow(),
                                AdminTestAccount.password().orElseThrow()
                        )))
                .andExpect(status().isOk());
    }
}
