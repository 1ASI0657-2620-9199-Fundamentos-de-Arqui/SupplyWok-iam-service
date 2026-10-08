package aurora.supply_wok.platform.iam.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.infrastructure.persistence.jpa.entities.UserPersistenceEntity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPersistenceAssemblerTests {

    @Test
    void toDomainFromPersistence_whenEntityProvided_mapsToDomainUser() {
        // Arrange
        var entity = new UserPersistenceEntity();
        entity.setId(15L);
        entity.setEmail("user@wok.pe");
        entity.setPassword("hash");
        entity.setRole(Roles.RESTAURANT);

        // Act
        var domain = UserPersistenceAssembler.toDomainFromPersistence(entity);

        // Assert
        assertThat(domain).isNotNull();
        assertThat(domain.getId()).isEqualTo(15L);
        assertThat(domain.getEmail()).isEqualTo("user@wok.pe");
        assertThat(domain.getPassword()).isEqualTo("hash");
        assertThat(domain.getRole()).isEqualTo(Roles.RESTAURANT);
    }

    @Test
    void toDomainFromPersistence_whenEntityIsNull_returnsNull() {
        // Arrange
        UserPersistenceEntity entity = null;

        // Act
        var domain = UserPersistenceAssembler.toDomainFromPersistence(entity);

        // Assert
        assertThat(domain).isNull();
    }

    @Test
    void toPersistenceFromDomain_whenDomainHasId_mapsAllFieldsIncludingId() {
        // Arrange
        var domain = new User("user@wok.pe", "hash", Roles.SUPPLIER);
        domain.setId(22L);

        // Act
        var entity = UserPersistenceAssembler.toPersistenceFromDomain(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isEqualTo(22L);
        assertThat(entity.getEmail()).isEqualTo("user@wok.pe");
        assertThat(entity.getPassword()).isEqualTo("hash");
        assertThat(entity.getRole()).isEqualTo(Roles.SUPPLIER);
    }

    @Test
    void toPersistenceFromDomain_whenDomainIdIsNull_leavesEntityIdNull() {
        // Arrange
        var domain = new User("user@wok.pe", "hash", Roles.SUPPLIER);

        // Act
        var entity = UserPersistenceAssembler.toPersistenceFromDomain(domain);

        // Assert
        assertThat(entity).isNotNull();
        assertThat(entity.getId()).isNull();
    }

    @Test
    void toPersistenceFromDomain_whenDomainIsNull_returnsNull() {
        // Arrange
        User domain = null;

        // Act
        var entity = UserPersistenceAssembler.toPersistenceFromDomain(domain);

        // Assert
        assertThat(entity).isNull();
    }
}
