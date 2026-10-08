package aurora.supply_wok.platform.profiles.domain.model.queries;

import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;

/**
 * Query to retrieve all account profiles for one role scope.
 */
public record GetProfilesByTypeQuery(EProfileType profileType) {
}
