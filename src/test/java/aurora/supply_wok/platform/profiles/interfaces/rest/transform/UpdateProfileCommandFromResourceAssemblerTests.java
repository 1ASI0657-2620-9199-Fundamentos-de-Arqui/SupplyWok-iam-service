package aurora.supply_wok.platform.profiles.interfaces.rest.transform;

import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateProfileCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenResourceProvided_mapsToUpdateProfileCommand() {
        // Arrange
        var resource = new UpdateProfileResource(
                "Sabor Wok", "Juan", "Diaz", "juan@wok.pe",
                "Av. Uno", "Distrito", "Lima", "Peru", "+51900000", false, true
        );

        // Act
        var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(EProfileType.RESTAURANT, resource);

        // Assert
        assertThat(command.profileType()).isEqualTo(EProfileType.RESTAURANT);
        assertThat(command.businessName()).isEqualTo("Sabor Wok");
        assertThat(command.firstName()).isEqualTo("Juan");
        assertThat(command.lastName()).isEqualTo("Diaz");
        assertThat(command.email()).isEqualTo("juan@wok.pe");
        assertThat(command.street()).isEqualTo("Av. Uno");
        assertThat(command.district()).isEqualTo("Distrito");
        assertThat(command.city()).isEqualTo("Lima");
        assertThat(command.country()).isEqualTo("Peru");
        assertThat(command.supportContact()).isEqualTo("+51900000");
        assertThat(command.emailNotifications()).isFalse();
        assertThat(command.smsNotifications()).isTrue();
    }
}
