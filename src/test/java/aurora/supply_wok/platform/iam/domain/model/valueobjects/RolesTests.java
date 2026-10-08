package aurora.supply_wok.platform.iam.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class RolesTests {

    @Test
    void valueOf_whenValidRoleNamesProvided_resolvesEnums() {
        // Arrange
        String restaurant = "RESTAURANT";
        String supplier = "SUPPLIER";
        String admin = "ADMIN";

        // Act
        var roleRestaurant = Roles.valueOf(restaurant);
        var roleSupplier = Roles.valueOf(supplier);
        var roleAdmin = Roles.valueOf(admin);

        // Assert
        assertThat(roleRestaurant).isEqualTo(Roles.RESTAURANT);
        assertThat(roleSupplier).isEqualTo(Roles.SUPPLIER);
        assertThat(roleAdmin).isEqualTo(Roles.ADMIN);
    }

    @Test
    void valueOf_whenUnknownRoleName_throwsIllegalArgumentException() {
        // Arrange
        String unknownRole = "UNKNOWN_ROLE";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> Roles.valueOf(unknownRole));
    }
}
