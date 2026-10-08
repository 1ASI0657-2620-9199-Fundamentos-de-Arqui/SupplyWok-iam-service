package aurora.supply_wok.platform.shared.infrastructure.messaging;

import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@ConditionalOnProperty(name = "supplywok.messaging.enabled", havingValue = "true", matchIfMissing = true)
@Slf4j
public class IntegrationEventRabbitPublisher {

    public static final String EXCHANGE_NAME = "supplywok.events";
    public static final String SUPPLIER_PROFILE_SYNC_ROUTING_KEY = "profiles.supplier.sync-requested";

    private final RabbitTemplate rabbitTemplate;

    public IntegrationEventRabbitPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    @TransactionalEventListener(fallbackExecution = true)
    public void handle(SupplierProfileSyncRequestedIntegrationEvent event) {
        try {
            rabbitTemplate.convertAndSend(EXCHANGE_NAME, SUPPLIER_PROFILE_SYNC_ROUTING_KEY, event);
        } catch (Exception e) {
            log.error("Failed to publish integration event {}: {}", event, e.getMessage());
        }
    }
}
