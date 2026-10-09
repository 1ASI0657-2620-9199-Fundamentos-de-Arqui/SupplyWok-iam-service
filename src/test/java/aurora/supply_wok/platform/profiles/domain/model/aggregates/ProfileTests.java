package aurora.supply_wok.platform.profiles.domain.model.aggregates;

import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.events.ProfileCreatedEvent;
import aurora.supply_wok.platform.profiles.domain.model.events.ProfileUpdatedEvent;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileTests {

    @Test
    void instantiate_withNoArgs_defaultsToRestaurantAndEmptyBusinessName() {
        // Arrange & Act
        var profile = new Profile();

        // Assert
        assertThat(profile.getProfileType()).isEqualTo(EProfileType.RESTAURANT);
        assertThat(profile.getBusinessName()).isEmpty();
        assertThat(profile.isEmailNotifications()).isTrue();
        assertThat(profile.isSmsNotifications()).isFalse();
        assertThat(profile.getFirstName()).isEmpty();
        assertThat(profile.getLastName()).isEmpty();
    }

    @Test
    void instantiate_withSupplierType_defaultsToEmptyBusinessName() {
        // Arrange & Act
        var profile = new Profile(EProfileType.SUPPLIER);

        // Assert
        assertThat(profile.getProfileType()).isEqualTo(EProfileType.SUPPLIER);
        assertThat(profile.getBusinessName()).isEmpty();
        assertThat(profile.isEmailNotifications()).isTrue();
        assertThat(profile.isSmsNotifications()).isFalse();
    }

    @Test
    void defaultFor_withProfileType_createsInstanceWithDefaults() {
        // Arrange & Act
        var profile = Profile.defaultFor(EProfileType.SUPPLIER);

        // Assert
        assertThat(profile.getProfileType()).isEqualTo(EProfileType.SUPPLIER);
        assertThat(profile.getBusinessName()).isEmpty();
    }

    @Test
    void instantiate_withFullConstructor_initializesAndNormalizesFields() {
        // Arrange & Act
        var profile = new Profile(
                EProfileType.SUPPLIER,
                "  Proveedor ABC  ",
                "  Maria  ",
                "  Perez  ",
                "  maria@abc.pe  ",
                "  Av. Central 123  ",
                "  Ate  ",
                "  Lima  ",
                "  Peru  ",
                "  +51987654321  ",
                false,
                true
        );

        // Assert
        assertThat(profile.getProfileType()).isEqualTo(EProfileType.SUPPLIER);
        assertThat(profile.getBusinessName()).isEqualTo("Proveedor ABC");
        assertThat(profile.getFirstName()).isEqualTo("Maria");
        assertThat(profile.getLastName()).isEqualTo("Perez");
        assertThat(profile.getEmail()).isEqualTo("maria@abc.pe");
        assertThat(profile.getStreet()).isEqualTo("Av. Central 123");
        assertThat(profile.getDistrict()).isEqualTo("Ate");
        assertThat(profile.getCity()).isEqualTo("Lima");
        assertThat(profile.getCountry()).isEqualTo("Peru");
        assertThat(profile.getSupportContact()).isEqualTo("+51987654321");
        assertThat(profile.isEmailNotifications()).isFalse();
        assertThat(profile.isSmsNotifications()).isTrue();
    }

    @Test
    void update_withUpdateProfileCommand_updatesAndTrimsFields() {
        // Arrange
        var profile = new Profile(EProfileType.RESTAURANT);
        var command = new UpdateProfileCommand(
                EProfileType.RESTAURANT,
                "  Restaurante El Wok  ",
                "  Carlos  ",
                "  Perez  ",
                "  carlos@wok.pe  ",
                "  Av. Lima 123  ",
                "  Miraflores  ",
                "  Lima  ",
                "  Peru  ",
                "  +51999999999  ",
                false,
                true
        );

        // Act
        profile.update(command);

        // Assert
        assertThat(profile.getBusinessName()).isEqualTo("Restaurante El Wok");
        assertThat(profile.getFirstName()).isEqualTo("Carlos");
        assertThat(profile.getLastName()).isEqualTo("Perez");
        assertThat(profile.getEmail()).isEqualTo("carlos@wok.pe");
        assertThat(profile.getStreet()).isEqualTo("Av. Lima 123");
        assertThat(profile.getDistrict()).isEqualTo("Miraflores");
        assertThat(profile.getCity()).isEqualTo("Lima");
        assertThat(profile.getCountry()).isEqualTo("Peru");
        assertThat(profile.getSupportContact()).isEqualTo("+51999999999");
        assertThat(profile.isEmailNotifications()).isFalse();
        assertThat(profile.isSmsNotifications()).isTrue();
    }

    @Test
    void update_whenFieldsAreNull_normalizesToEmptyStrings() {
        // Arrange
        var profile = new Profile(EProfileType.RESTAURANT);
        var command = new UpdateProfileCommand(
                EProfileType.RESTAURANT,
                null, null, null, null, null, null, null, null, null, true, false
        );

        // Act
        profile.update(command);

        // Assert
        assertThat(profile.getBusinessName()).isEmpty();
        assertThat(profile.getFirstName()).isEmpty();
        assertThat(profile.getLastName()).isEmpty();
        assertThat(profile.getEmail()).isEmpty();
        assertThat(profile.getStreet()).isEmpty();
        assertThat(profile.getDistrict()).isEmpty();
        assertThat(profile.getCity()).isEmpty();
        assertThat(profile.getCountry()).isEmpty();
        assertThat(profile.getSupportContact()).isEmpty();
    }

    @Test
    void onCreated_whenInvoked_registersProfileCreatedDomainEvent() {
        // Arrange
        var profile = new Profile(EProfileType.RESTAURANT);
        profile.setId(12L);

        // Act
        profile.onCreated();

        // Assert
        assertThat(profile.domainEvents()).hasSize(1);
        var event = (ProfileCreatedEvent) profile.domainEvents().iterator().next();
        assertThat(event.profileId()).isEqualTo(12L);
        assertThat(event.profileType()).isEqualTo("RESTAURANT");
        assertThat(event.businessName()).isEmpty();
    }

    @Test
    void onUpdated_whenInvoked_registersProfileUpdatedDomainEvent() {
        // Arrange
        var profile = new Profile(EProfileType.SUPPLIER);
        profile.setId(25L);

        // Act
        profile.onUpdated();

        // Assert
        assertThat(profile.domainEvents()).hasSize(1);
        var event = (ProfileUpdatedEvent) profile.domainEvents().iterator().next();
        assertThat(event.profileId()).isEqualTo(25L);
        assertThat(event.profileType()).isEqualTo("SUPPLIER");
        assertThat(event.businessName()).isEmpty();
    }
}
