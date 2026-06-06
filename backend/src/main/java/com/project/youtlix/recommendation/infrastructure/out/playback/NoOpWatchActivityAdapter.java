package com.project.youtlix.recommendation.infrastructure.out.playback;

import com.project.youtlix.recommendation.application.port.out.WatchActivity;
import com.project.youtlix.recommendation.application.port.out.WatchActivityPort;
import com.project.youtlix.recommendation.domain.model.ViewerId;
import org.springframework.stereotype.Component;
import java.util.List;

/** Placeholder adapter for playback watch activity until playback integration is implemented. */
@Component
public class NoOpWatchActivityAdapter implements WatchActivityPort {
    @Override public List<WatchActivity> watchedBy(ViewerId viewerId) { return List.of(); }
}
