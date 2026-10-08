package aurora.supply_wok.platform.profiles.application.internal.commandservices;

import aurora.supply_wok.platform.profiles.application.commandservices.ProfileCommandService;
import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@RecordApplicationEvents
class ProfileCommandServiceImplIntegrationTests {

    @Autowired
    private ProfileCommandService profileCommandService;

    @Autowired
    private ApplicationEvents applicationEvents;

    @Test
    void handle_whenUpdatingSupplierProfile_publishesSupplierProfileSyncRequestedIntegrationEvent() {
        // Arrange
        var command = new UpdateProfileCommand(
                EProfileType.SUPPLIER,
                "Insumos Andinos S.A.C.",
                "Maria",
                "Quispe",
                "contacto@insumosandinos.pe",
                "Av. Separadora Industrial 123",
                "Ate",
                "Lima",
                "Peru",
                "+51987654321",
                true,
                false
        );

        // Act
        var profile = profileCommandService.handle(command);

        // Assert
        assertThat(profile).isNotNull();

        var events = applicationEvents.stream(SupplierProfileSyncRequestedIntegrationEvent.class).toList();
        assertThat(events).isNotEmpty();

        var matchingEvent = events.stream()
                .filter(e -> "contacto@insumosandinos.pe".equals(e.email()))
                .findFirst();

        assertThat(matchingEvent).isPresent();
        assertThat(matchingEvent.get().name()).isEqualTo("Insumos Andinos S.A.C.");
        assertThat(matchingEvent.get().contactName()).isEqualTo("Maria Quispe");
        assertThat(matchingEvent.get().phone()).isEqualTo("+51987654321");
    }

    @Test
    void handle_whenUpdatingRestaurantProfile_doesNotPublishSupplierSyncEvent() {
        // Arrange
        var command = new UpdateProfileCommand(
                EProfileType.RESTAURANT,
                "El Buen Sabor",
                "Pedro",
                "Ramirez",
                "pedro@buensabor.pe",
                "Av. Primavera 456",
                "Surco",
                "Lima",
                "Peru",
                "+51912345678",
                true,
                false
        );

        // Act
        var profile = profileCommandService.handle(command);

        // Assert
        assertThat(profile).isNotNull();

        var matchingEvent = applicationEvents.stream(SupplierProfileSyncRequestedIntegrationEvent.class)
                .filter(e -> "pedro@buensabor.pe".equals(e.email()))
                .findFirst();

        assertThat(matchingEvent).isEmpty();
    }
}
