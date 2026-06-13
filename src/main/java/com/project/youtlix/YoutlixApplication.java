package com.project.youtlix;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Youtlix modular monolith.
 *
 * <p>The application follows ports and adapters: each bounded context owns its
 * domain and application code, while web, persistence, Supabase and CDN details
 * stay in infrastructure adapters.</p>
 */
@SpringBootApplication
public class YoutlixApplication {

    /**
     * Starts the Spring Boot application.
     *
     * @param args command line arguments passed to Spring Boot
     */
    public static void main(String[] args) {
        SpringApplication.run(YoutlixApplication.class, args);
    }
}
