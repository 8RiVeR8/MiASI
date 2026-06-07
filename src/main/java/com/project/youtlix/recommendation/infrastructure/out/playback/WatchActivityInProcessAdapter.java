package com.project.youtlix.recommendation.infrastructure.out.playback;

import com.project.youtlix.authentication.domain.model.ViewerId;
import com.project.youtlix.recommendation.application.port.out.WatchActivityPort;
import com.project.youtlix.videoplayback.application.port.in.WatchActivityApi;
import com.project.youtlix.videoplayback.domain.model.WatchActivity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * In-process conformist adapter from recommendation to playback activity OHS.
 */
@Component
public class WatchActivityInProcessAdapter implements WatchActivityPort {

    private final WatchActivityApi watchActivityApi;

    /** Creates adapter around playback published port. */
    public WatchActivityInProcessAdapter(WatchActivityApi watchActivityApi) {
        this.watchActivityApi = watchActivityApi;
    }

    @Override
    public List<WatchActivity> watchedBy(ViewerId viewerId) {
        return watchActivityApi.watchedBy(viewerId);
    }
}
