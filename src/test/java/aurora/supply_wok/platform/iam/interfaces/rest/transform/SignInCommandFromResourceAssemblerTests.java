package aurora.supply_wok.platform.iam.interfaces.rest.transform;

import aurora.supply_wok.platform.iam.interfaces.rest.resources.SignInResource;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SignInCommandFromResourceAssemblerTests {

    @Test
    void toCommandFromResource_whenResourceProvided_mapsToSignInCommand() {
        // Arrange
        var resource = new SignInResource("user@wok.pe", "password123");

        // Act
        var command = SignInCommandFromResourceAssembler.toCommandFromResource(resource);

        // Assert
        assertThat(command.email()).isEqualTo("user@wok.pe");
        assertThat(command.password()).isEqualTo("password123");
    }
}
