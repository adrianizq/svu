package co.edu.itp.svu.web.rest;

import co.edu.itp.svu.security.SecurityUtils;
import co.edu.itp.svu.service.SseNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/sse-notifications")
public class SseNotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(SseNotificationResource.class);
    private final SseNotificationService sseNotificationService;

    public SseNotificationResource(SseNotificationService sseNotificationService) {
        this.sseNotificationService = sseNotificationService;
    }

    @GetMapping(value = "/subscribe", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<SseEmitter> subscribeToNotifications() {
        String userLogin = SecurityUtils.getCurrentUserLogin().orElseThrow(() -> new RuntimeException("Current user login not found"));
        LOG.info("User {} subscribing to SSE notifications", userLogin);
        SseEmitter emitter = sseNotificationService.createEmitter(userLogin);

        return ResponseEntity.ok(emitter);
    }
}
