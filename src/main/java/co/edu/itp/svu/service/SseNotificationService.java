package co.edu.itp.svu.service;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * Service implementation for managing the SSE emitters(connections)
 */
@Service
public class SseNotificationService {

    private static final Logger LOG = LoggerFactory.getLogger(SseNotificationService.class);
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();
    private static final Long EMITTER_TIMEOUT = 30 * 60 * 1000L;

    public SseEmitter createEmitter(String userLogin) {
        SseEmitter emitter = new SseEmitter(EMITTER_TIMEOUT);
        this.emitters.put(userLogin, emitter);

        emitter.onCompletion(() -> {
            LOG.info("SseEmitter completed for user: {}", userLogin);
            this.emitters.remove(userLogin);
        });

        emitter.onTimeout(() -> {
            LOG.info("SseEmitter timed out for user: {}", userLogin);
            emitter.complete();
        });

        emitter.onError(e -> {
            LOG.error("SseEmitter error for user: {}: {}", userLogin, e.getMessage());
            emitter.completeWithError(e);
        });

        try {
            emitter.send(SseEmitter.event().name("connected").data("SSE connection established for " + userLogin));
        } catch (IOException e) {
            LOG.error("Error sending initial connection event to {}: {}", userLogin, e.getMessage());
            emitter.completeWithError(e);
        }

        LOG.info("SseEmitter created for user: {}", userLogin);
        return emitter;
    }

    public void sendNotificationToUser(String userLogin, String eventName, Object data) {
        SseEmitter emitter = this.emitters.get(userLogin);
        if (emitter != null) {
            try {
                LOG.debug("Sending SSE event '{}' to user {}: {}", eventName, userLogin, data);
                emitter.send(SseEmitter.event().name(eventName).data(data));
            } catch (IOException e) {
                LOG.error("Error sending SSE event to user {}: {}. Removing emitter.", userLogin, e.getMessage());
                emitter.completeWithError(e);
            }
        } else {
            LOG.warn("No active SseEmitter found for user: {}", userLogin);
        }
    }
}
