package aurora.supply_wok.platform.iam.application.internal.eventhandlers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * Application lifecycle handler that log readiness.
 */
@Service
@Slf4j
public class ApplicationReadyEventHandler {

    public ApplicationReadyEventHandler() {
    }

    /**
     * Handles the Spring application-ready event.
     *
     * @param event Spring Boot readiness event
     */
    @EventListener
    public void on(ApplicationReadyEvent event) {
        var applicationName = event.getApplicationContext().getId();
        log.info("Application {} is ready at {}", applicationName, currentTimestamp());
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }
}
