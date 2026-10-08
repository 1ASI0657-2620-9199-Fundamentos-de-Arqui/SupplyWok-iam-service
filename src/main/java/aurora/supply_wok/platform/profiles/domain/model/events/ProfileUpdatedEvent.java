package aurora.supply_wok.platform.profiles.domain.model.events;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;

/**
 * Domain event raised when profile settings are updated.
 */
public record ProfileUpdatedEvent(
        Long profileId,
        String profileType,
        String businessName,
        String email
) {
    public static ProfileUpdatedEvent from(Profile profile) {
        return new ProfileUpdatedEvent(
                profile.getId(),
                profile.getProfileType().name(),
                profile.getBusinessName(),
                profile.getEmail()
        );
    }
}
