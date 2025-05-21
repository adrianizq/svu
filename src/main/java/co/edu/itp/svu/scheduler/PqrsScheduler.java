package co.edu.itp.svu.scheduler;

import co.edu.itp.svu.domain.Pqrs;
import co.edu.itp.svu.domain.User;
import co.edu.itp.svu.repository.PqrsRepository;
import co.edu.itp.svu.repository.UserRepository;
import co.edu.itp.svu.service.SseNotificationService;
import co.edu.itp.svu.service.dto.PqrsDTO;
import co.edu.itp.svu.service.mapper.PqrsMapper;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class PqrsScheduler {

    private static final Logger LOG = LoggerFactory.getLogger(PqrsScheduler.class);

    private final PqrsRepository pqrsRepository;
    private final SseNotificationService sseNotificationService;
    private final PqrsMapper pqrsMapper;
    private final UserRepository userRepository;

    public PqrsScheduler(
        PqrsRepository pqrsRepository,
        SseNotificationService sseNotificationService,
        PqrsMapper pqrsMapper,
        UserRepository userRepository
    ) {
        this.pqrsRepository = pqrsRepository;
        this.sseNotificationService = sseNotificationService;
        this.pqrsMapper = pqrsMapper;
        this.userRepository = userRepository;
    }

    // @Scheduled(cron = "0 0 * * * ?") // Every hour
    @Scheduled(fixedRate = 10000) // Every minute for testing
    public void checkPqrsDueDates() {
        LOG.info("Running PQRS due date check...");

        Instant now = Instant.now();
        // Instant oneDayFromNow = now.plus(1, ChronoUnit.DAYS);
        Instant threeDaysFromNow = now.plus(3, ChronoUnit.DAYS);

        List<Pqrs> upcomingPqrs = pqrsRepository.findAllByFechaLimiteRespuestaBetweenAndEstadoNotIn(
            now,
            threeDaysFromNow,
            List.of("Resuelta", "closed")
        );

        for (Pqrs pqrs : upcomingPqrs) {
            LOG.debug(
                "Found upcoming PQRS: id={}, title={}, dueDate={}",
                pqrs.getTitulo(),
                pqrs.getTitulo(),
                pqrs.getFechaLimiteRespuesta()
            );

            if (pqrs.getOficinaResponder() != null) {
                User responsibleUser = pqrs.getOficinaResponder().getResponsable();

                if (responsibleUser != null) {
                    sendPqrsNotification(responsibleUser.getLogin(), pqrs);
                }
            } else {
                LOG.warn("PQRS with id {} has no OficinaResponder, cannot determine responsible user.", pqrs.getId());
            }
        }
        LOG.info("PQRS due date check finished.");
    }

    private void sendPqrsNotification(String userLogin, Pqrs pqrs) {
        String message = String.format(
            "PQRS '%s' (ID: %s) is due on %s.",
            pqrs.getTitulo(),
            pqrs.getId(),
            pqrs.getFechaLimiteRespuesta().toString()
        );

        // PqrsDTO pqrsDTO = pqrsMapper.toDto(pqrs);

        var notificationData = Map.of(
            "id",
            pqrs.getId(),
            "type",
            "PQRS_DUE_DATE_REMINDER",
            "currentDate",
            LocalDate.now(),
            "message",
            message,
            "read",
            false,
            "userLogin",
            userLogin,
            "pqrsId",
            pqrs.getId(),
            "title",
            pqrs.getTitulo(),
            "pqrsResponseDueDate",
            pqrs.getFechaLimiteRespuesta(),
            "isSse",
            true
        );

        sseNotificationService.sendNotificationToUser(userLogin, "PQRS_DUE_DATE_REMINDER", notificationData);

        LOG.info("Sent due date reminder for PQRS id {} to user {}", pqrs.getId(), userLogin);
    }
}
