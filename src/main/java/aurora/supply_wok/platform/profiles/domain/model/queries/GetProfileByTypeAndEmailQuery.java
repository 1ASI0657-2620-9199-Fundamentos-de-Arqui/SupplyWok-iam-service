package aurora.supply_wok.platform.profiles.domain.model.queries;

import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;

/**
 * Query to retrieve one profile by role scope and account email.
 */
public record GetProfileByTypeAndEmailQuery(EProfileType profileType, String email) {
}
