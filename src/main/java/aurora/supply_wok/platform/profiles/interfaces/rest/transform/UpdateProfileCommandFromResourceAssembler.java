package aurora.supply_wok.platform.profiles.interfaces.rest.transform;

import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.interfaces.rest.resources.UpdateProfileResource;

/**
 * Maps profile update requests to commands.
 */
public final class UpdateProfileCommandFromResourceAssembler {

    private UpdateProfileCommandFromResourceAssembler() {
    }

    public static UpdateProfileCommand toCommandFromResource(EProfileType profileType, UpdateProfileResource resource) {
        return new UpdateProfileCommand(
                profileType,
                resource.businessName(),
                resource.firstName(),
                resource.lastName(),
                resource.email(),
                resource.street(),
                resource.district(),
                resource.city(),
                resource.country(),
                resource.supportContact(),
                resource.emailNotifications(),
                resource.smsNotifications()
        );
    }
}
