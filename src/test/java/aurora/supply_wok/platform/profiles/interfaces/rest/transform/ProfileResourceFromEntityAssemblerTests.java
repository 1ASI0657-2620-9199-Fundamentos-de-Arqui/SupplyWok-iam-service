package aurora.supply_wok.platform.profiles.interfaces.rest.transform;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfileResourceFromEntityAssemblerTests {

    @Test
    void toResourceFromEntity_whenProfileProvided_mapsToProfileResource() {
        // Arrange
        var profile = new Profile(
                EProfileType.RESTAURANT, "Sabor Wok", "Juan", "Diaz", "juan@wok.pe",
                "Av. Uno", "Distrito", "Lima", "Peru", "+51900000", true, false
        );
        profile.setId(55L);

        // Act
        var resource = ProfileResourceFromEntityAssembler.toResourceFromEntity(profile);

        // Assert
        assertThat(resource.id()).isEqualTo(55L);
        assertThat(resource.profileType()).isEqualTo("RESTAURANT");
        assertThat(resource.businessName()).isEqualTo("Sabor Wok");
        assertThat(resource.firstName()).isEqualTo("Juan");
        assertThat(resource.lastName()).isEqualTo("Diaz");
        assertThat(resource.email()).isEqualTo("juan@wok.pe");
        assertThat(resource.street()).isEqualTo("Av. Uno");
        assertThat(resource.district()).isEqualTo("Distrito");
        assertThat(resource.city()).isEqualTo("Lima");
        assertThat(resource.country()).isEqualTo("Peru");
        assertThat(resource.supportContact()).isEqualTo("+51900000");
        assertThat(resource.emailNotifications()).isTrue();
        assertThat(resource.smsNotifications()).isFalse();
    }
}
