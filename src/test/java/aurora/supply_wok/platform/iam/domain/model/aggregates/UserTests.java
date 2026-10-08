package aurora.supply_wok.platform.iam.domain.model.aggregates;

import aurora.supply_wok.platform.iam.domain.model.events.UserSignedUpEvent;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserTests {

    @Test
    void instantiate_withNoArgs_initializesFieldsAndSupportsSetters() {
        // Arrange
        var user = new User();

        // Act
        user.setId(10L);
        user.setEmail("user@test.com");
        user.setPassword("secret");
        user.setRole(Roles.RESTAURANT);

        // Assert
        assertThat(user.getId()).isEqualTo(10L);
        assertThat(user.getEmail()).isEqualTo("user@test.com");
        assertThat(user.getPassword()).isEqualTo("secret");
        assertThat(user.getRole()).isEqualTo(Roles.RESTAURANT);
    }

    @Test
    void instantiate_withEmailAndPassword_setsEmailAndPassword() {
        // Arrange
        String email = "chef@supplywok.com";
        String password = "Password123!";

        // Act
        var user = new User(email, password);

        // Assert
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isNull();
    }

    @Test
    void instantiate_withEmailPasswordAndRole_setsAllProperties() {
        // Arrange
        String email = "supplier@supplywok.com";
        String password = "Password123!";
        Roles role = Roles.SUPPLIER;

        // Act
        var user = new User(email, password, role);

        // Assert
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getRole()).isEqualTo(role);
    }

    @Test
    void onSignedUp_whenInvoked_registersUserSignedUpDomainEvent() {
        // Arrange
        var user = new User("chef@supplywok.com", "secret", Roles.RESTAURANT);
        user.setId(5L);

        // Act
        user.onSignedUp();

        // Assert
        assertThat(user.domainEvents()).hasSize(1);
        var event = (UserSignedUpEvent) user.domainEvents().iterator().next();
        assertThat(event.userId()).isEqualTo(5L);
        assertThat(event.email()).isEqualTo("chef@supplywok.com");
        assertThat(event.roles()).containsExactly("RESTAURANT");
    }

    @Test
    void clearDomainEvents_whenInvoked_removesAllRegisteredDomainEvents() {
        // Arrange
        var user = new User("chef@supplywok.com", "secret", Roles.RESTAURANT);
        user.onSignedUp();

        // Act
        user.clearDomainEvents();

        // Assert
        assertThat(user.domainEvents()).isEmpty();
    }
}
