package com.project.youtlix.controller.playback;

import com.project.youtlix.dto.PlaybackRequest;
import com.project.youtlix.dto.PlaybackResponse;
import com.project.youtlix.entity.playback.Playback;
import com.project.youtlix.service.PlaybackService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/playback")
public class PlaybackController {

    private final PlaybackService service;

    public PlaybackController(PlaybackService service) {
        this.service = service;
    }

    @PostMapping("/{viewerId}")
    public PlaybackResponse save(
            @PathVariable UUID viewerId,
            @RequestBody PlaybackRequest request
    ) {
        Playback p = service.saveProgress(
                viewerId,
                request.getPlayableId(),
                request.getPlayableType(),
                request.getPositionSeconds(),
                1000 // placeholder - później z Content.duration
        );

        return new PlaybackResponse(
                p.getPlayableId(),
                p.getPlayableType(),
                p.getPositionSeconds(),
                p.getStatus().name()
        );
    }

    @GetMapping("/{viewerId}/continue")
    public List<PlaybackResponse> continueWatching(@PathVariable UUID viewerId) {

        return service.getContinueWatching(viewerId)
                .stream()
                .map(p -> new PlaybackResponse(
                        p.getPlayableId(),
                        p.getPlayableType(),
                        p.getPositionSeconds(),
                        p.getStatus().name()
                ))
                .toList();
    }
}
