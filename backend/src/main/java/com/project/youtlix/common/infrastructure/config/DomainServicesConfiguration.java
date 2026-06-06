package com.project.youtlix.common.infrastructure.config;

import com.project.youtlix.authentication.domain.service.AuthenticationService;
import com.project.youtlix.authentication.domain.service.PasswordResetService;
import com.project.youtlix.authentication.domain.service.RegistrationService;
import com.project.youtlix.authentication.domain.service.ViewerAccountFactory;
import com.project.youtlix.contentlibrary.domain.service.ContentFactory;
import com.project.youtlix.contentlibrary.domain.service.ContentSearchService;
import com.project.youtlix.recommendation.domain.service.RatingService;
import com.project.youtlix.recommendation.domain.service.RecommendationEngine;
import com.project.youtlix.videoplayback.domain.service.PlaybackService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Spring configuration that wires pure domain services without putting Spring in the domain layer. */
@Configuration
public class DomainServicesConfiguration {
    @Bean public ViewerAccountFactory viewerAccountFactory() { return new ViewerAccountFactory(); }
    @Bean public RegistrationService registrationService(ViewerAccountFactory factory) { return new RegistrationService(factory); }
    @Bean public AuthenticationService authenticationService() { return new AuthenticationService(); }
    @Bean public PasswordResetService passwordResetService() { return new PasswordResetService(); }
    @Bean public ContentFactory contentFactory() { return new ContentFactory(); }
    @Bean public ContentSearchService contentSearchService() { return new ContentSearchService(); }
    @Bean public RatingService ratingService() { return new RatingService(); }
    @Bean public RecommendationEngine recommendationEngine() { return new RecommendationEngine(); }
    @Bean public PlaybackService playbackService() { return new PlaybackService(); }
}
