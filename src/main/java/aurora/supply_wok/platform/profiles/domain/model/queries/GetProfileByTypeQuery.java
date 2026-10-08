package aurora.supply_wok.platform.profiles.domain.model.queries;

import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;

/**
 * Query to retrieve a profile by role scope.
 */
public record GetProfileByTypeQuery(EProfileType profileType) {
}
