package aurora.supply_wok.platform.profiles.interfaces.rest.transform;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.interfaces.rest.resources.ProfileResource;

/**
 * Maps profile aggregates to REST resources.
 */
public final class ProfileResourceFromEntityAssembler {

    private ProfileResourceFromEntityAssembler() {
    }

    public static ProfileResource toResourceFromEntity(Profile profile) {
        return new ProfileResource(
                profile.getId(),
                profile.getProfileType().name(),
                profile.getBusinessName(),
                profile.getFirstName(),
                profile.getLastName(),
                profile.getEmail(),
                profile.getStreet(),
                profile.getDistrict(),
                profile.getCity(),
                profile.getCountry(),
                profile.getSupportContact(),
                profile.isEmailNotifications(),
                profile.isSmsNotifications()
        );
    }
}
