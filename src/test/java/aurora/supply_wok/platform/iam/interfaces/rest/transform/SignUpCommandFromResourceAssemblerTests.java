package aurora.supply_wok.platform.iam.interfaces.rest.transform;

import aurora.supply_wok.platform.iam.interfaces.rest.resources.SignUpResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignUpCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenResourceProvided_mapsToSignUpCommand() {
        // Arrange
        var resource = new SignUpResource("user@wok.pe", "password123", "RESTAURANT");

        // Act
        var command = SignUpCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.email()).isEqualTo("user@wok.pe");
        assertThat(command.password()).isEqualTo("password123");
        assertThat(command.role()).isEqualTo("RESTAURANT");
    }
}
