package aurora.supply_wok.platform.shared.infrastructure.messaging;

import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class IntegrationEventRabbitPublisherTests {

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private IntegrationEventRabbitPublisher publisher;

    @Test
    void handle_whenEventReceived_publishesToRabbitTemplate() {
        // Arrange
        var event = new SupplierProfileSyncRequestedIntegrationEvent(
                "Proveedor Wok", "Juan Perez", "juan@proveedor.pe", "+51988888"
        );

        // Act
        publisher.handle(event);

        // Assert
        verify(rabbitTemplate).convertAndSend(
                IntegrationEventRabbitPublisher.EXCHANGE_NAME,
                IntegrationEventRabbitPublisher.SUPPLIER_PROFILE_SYNC_ROUTING_KEY,
                event
        );
    }

    @Test
    void handle_whenRabbitTemplateThrowsException_catchesAndLogsWithoutThrowing() {
        // Arrange
        var event = new SupplierProfileSyncRequestedIntegrationEvent(
                "Proveedor Wok", "Juan Perez", "juan@proveedor.pe", "+51988888"
        );
        doThrow(new RuntimeException("RabbitMQ connection refused"))
                .when(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));

        // Act & Assert
        assertDoesNotThrow(() -> publisher.handle(event));
        verify(rabbitTemplate).convertAndSend(any(String.class), any(String.class), any(Object.class));
    }
}
