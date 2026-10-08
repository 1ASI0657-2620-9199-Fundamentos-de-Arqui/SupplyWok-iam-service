package aurora.supply_wok.platform.profiles.application.internal.queryservices;

import aurora.supply_wok.platform.profiles.application.queryservices.ProfileQueryService;
import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeAndEmailQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfilesByTypeQuery;
import aurora.supply_wok.platform.profiles.domain.repositories.ProfileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Default profile query service implementation.
 */
@Service
public class ProfileQueryServiceImpl implements ProfileQueryService {

    private final ProfileRepository profileRepository;

    public ProfileQueryServiceImpl(ProfileRepository profileRepository) {
        this.profileRepository = profileRepository;
    }

    @Override
    public Optional<Profile> handle(GetProfileByTypeQuery query) {
        return profileRepository.findByProfileType(query.profileType());
    }

    @Override
    public Optional<Profile> handle(GetProfileByTypeAndEmailQuery query) {
        return profileRepository.findByProfileTypeAndEmail(query.profileType(), query.email());
    }

    @Override
    public List<Profile> handle(GetProfilesByTypeQuery query) {
        return profileRepository.findAllByProfileType(query.profileType());
    }
}
