package aurora.supply_wok.platform.shared.infrastructure.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AuthenticatedUserTests {

    @Test
    void hasRole_matchingRoleWithoutPrefix_returnsTrue() {
        // Arrange
        var user = new AuthenticatedUser(1L, "user@supplywok.pe", List.of("RESTAURANT"));

        // Act
        var result = user.hasRole("RESTAURANT");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void hasRole_matchingRoleWithPrefix_returnsTrue() {
        // Arrange
        var user = new AuthenticatedUser(1L, "user@supplywok.pe", List.of("ROLE_ADMIN"));

        // Act
        var result = user.hasRole("ADMIN");

        // Assert
        assertThat(result).isTrue();
    }

    @Test
    void hasRole_differentRole_returnsFalse() {
        // Arrange
        var user = new AuthenticatedUser(1L, "user@supplywok.pe", List.of("SUPPLIER"));

        // Act
        var result = user.hasRole("ADMIN");

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void hasRole_nullRole_returnsFalse() {
        // Arrange
        var user = new AuthenticatedUser(1L, "user@supplywok.pe", List.of("ADMIN"));

        // Act
        var result = user.hasRole(null);

        // Assert
        assertThat(result).isFalse();
    }

    @Test
    void constructor_nullRolesList_initializesEmptyList() {
        // Arrange & Act
        var user = new AuthenticatedUser(1L, "user@supplywok.pe", null);

        // Assert
        assertThat(user.roles()).isNotNull().isEmpty();
        assertThat(user.hasRole("ADMIN")).isFalse();
    }
}
