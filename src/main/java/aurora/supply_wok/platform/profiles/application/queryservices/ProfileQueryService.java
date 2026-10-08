package aurora.supply_wok.platform.profiles.application.queryservices;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeAndEmailQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfilesByTypeQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application contract for profile read use cases.
 */
public interface ProfileQueryService {
    Optional<Profile> handle(GetProfileByTypeQuery query);

    Optional<Profile> handle(GetProfileByTypeAndEmailQuery query);

    List<Profile> handle(GetProfilesByTypeQuery query);
}
