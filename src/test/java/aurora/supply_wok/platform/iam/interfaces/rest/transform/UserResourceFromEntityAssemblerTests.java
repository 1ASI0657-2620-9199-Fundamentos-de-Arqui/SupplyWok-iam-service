package aurora.supply_wok.platform.iam.interfaces.rest.transform;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenUserProvided_mapsToUserResource() {
        // Arrange
        var user = new User("user@wok.pe", "hash", Roles.SUPPLIER);
        user.setId(101L);

        // Act
        var resource = UserResourceFromEntityAssembler.toResourceFromEntity(user);

        // Assert
        assertThat(resource.id()).isEqualTo(101L);
        assertThat(resource.email()).isEqualTo("user@wok.pe");
        assertThat(resource.role()).isEqualTo("SUPPLIER");
    }
}
