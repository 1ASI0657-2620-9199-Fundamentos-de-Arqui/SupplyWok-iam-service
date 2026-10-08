package aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ProfilePersistenceAssemblerTests {

    @Test
    void toDomainFromPersistence_whenEntityProvided_mapsToDomainProfile() {
        // Arrange
        var entity = new ProfilePersistenceEntity();
        entity.setId(33L);
        entity.setProfileType(EProfileType.SUPPLIER);
        entity.setBusinessName("Distribuidora");
        entity.setFirstName("Ana");
        entity.setLastName("Gomez");
        entity.setEmail("ana@dist.pe");
        entity.setStreet("Jr. Puno 100");
        entity.setDistrict("Cercado");
        entity.setCity("Lima");
        entity.setCountry("Peru");
        entity.setSupportContact("+51911111");
        entity.setEmailNotifications(true);
        entity.setSmsNotifications(false);

        // Act
        var domain = ProfilePersistenceAssembler.toDomainFromPersistence(entity);

        // Assert
        assertThat(domain.getId()).isEqualTo(33L);
        assertThat(domain.getProfileType()).isEqualTo(EProfileType.SUPPLIER);
        assertThat(domain.getBusinessName()).isEqualTo("Distribuidora");
        assertThat(domain.getFirstName()).isEqualTo("Ana");
        assertThat(domain.getLastName()).isEqualTo("Gomez");
        assertThat(domain.getEmail()).isEqualTo("ana@dist.pe");
        assertThat(domain.getStreet()).isEqualTo("Jr. Puno 100");
        assertThat(domain.getDistrict()).isEqualTo("Cercado");
        assertThat(domain.getCity()).isEqualTo("Lima");
        assertThat(domain.getCountry()).isEqualTo("Peru");
        assertThat(domain.getSupportContact()).isEqualTo("+51911111");
        assertThat(domain.isEmailNotifications()).isTrue();
        assertThat(domain.isSmsNotifications()).isFalse();
    }

    @Test
    void toPersistenceFromDomain_whenDomainProvided_mapsToPersistenceEntity() {
        // Arrange
        var domain = new Profile(
                EProfileType.RESTAURANT, "Wok Express", "Leo", "Messi", "leo@wok.pe",
                "Av. Central 500", "Lince", "Lima", "Peru", "+51922222", false, true
        );
        domain.setId(44L);

        // Act
        var entity = ProfilePersistenceAssembler.toPersistenceFromDomain(domain);

        // Assert
        assertThat(entity.getId()).isEqualTo(44L);
        assertThat(entity.getProfileType()).isEqualTo(EProfileType.RESTAURANT);
        assertThat(entity.getBusinessName()).isEqualTo("Wok Express");
        assertThat(entity.getFirstName()).isEqualTo("Leo");
        assertThat(entity.getLastName()).isEqualTo("Messi");
        assertThat(entity.getEmail()).isEqualTo("leo@wok.pe");
        assertThat(entity.getStreet()).isEqualTo("Av. Central 500");
        assertThat(entity.getDistrict()).isEqualTo("Lince");
        assertThat(entity.getCity()).isEqualTo("Lima");
        assertThat(entity.getCountry()).isEqualTo("Peru");
        assertThat(entity.getSupportContact()).isEqualTo("+51922222");
        assertThat(entity.isEmailNotifications()).isFalse();
        assertThat(entity.isSmsNotifications()).isTrue();
    }
}
