package aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.repositories;

import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data repository for profile persistence entities.
 */
@Repository
public interface ProfilePersistenceRepository extends JpaRepository<ProfilePersistenceEntity, Long> {
    Optional<ProfilePersistenceEntity> findByProfileType(EProfileType profileType);

    Optional<ProfilePersistenceEntity> findByProfileTypeAndEmailIgnoreCase(EProfileType profileType, String email);

    List<ProfilePersistenceEntity> findAllByProfileType(EProfileType profileType);
}
