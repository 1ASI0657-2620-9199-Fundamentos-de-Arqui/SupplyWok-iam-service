package aurora.supply_wok.platform.profiles.domain.model.events;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;

/**
 * Domain event raised when a profile is created.
 */
public record ProfileCreatedEvent(
        Long profileId,
        String profileType,
        String businessName,
        String email
) {
    public static ProfileCreatedEvent from(Profile profile) {
        return new ProfileCreatedEvent(
                profile.getId(),
                profile.getProfileType().name(),
                profile.getBusinessName(),
                profile.getEmail()
        );
    }
}
