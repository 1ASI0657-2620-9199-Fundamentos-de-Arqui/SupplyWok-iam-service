package aurora.supply_wok.platform.profiles.domain.repositories;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;

import java.util.Optional;
import java.util.List;

/**
 * Profile repository port.
 */
public interface ProfileRepository {
    Optional<Profile> findByProfileType(EProfileType profileType);

    Optional<Profile> findByProfileTypeAndEmail(EProfileType profileType, String email);

    List<Profile> findAllByProfileType(EProfileType profileType);

    Profile save(Profile profile);
}
