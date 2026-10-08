package aurora.supply_wok.platform.iam.interfaces.rest.transform;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedUserResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenUserHasRole_mapsAllProperties() {
        // Arrange
        var user = new User("test@wok.pe", "hash", Roles.RESTAURANT);
        user.setId(42L);
        String token = "jwt.sample.token";

        // Act
        var resource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, token);

        // Assert
        assertThat(resource.id()).isEqualTo(42L);
        assertThat(resource.email()).isEqualTo("test@wok.pe");
        assertThat(resource.token()).isEqualTo(token);
        assertThat(resource.role()).isEqualTo("RESTAURANT");
        assertThat(resource.roles()).containsExactly("RESTAURANT");
    }

    @Test
    void toResourceFromEntity_whenUserRoleIsNull_mapsNullRoleAndEmptyRoles() {
        // Arrange
        var user = new User("test@wok.pe", "hash");
        user.setId(42L);
        String token = "jwt.sample.token";

        // Act
        var resource = AuthenticatedUserResourceFromEntityAssembler.toResourceFromEntity(user, token);

        // Assert
        assertThat(resource.id()).isEqualTo(42L);
        assertThat(resource.email()).isEqualTo("test@wok.pe");
        assertThat(resource.token()).isEqualTo(token);
        assertThat(resource.role()).isNull();
        assertThat(resource.roles()).isEmpty();
    }
}
