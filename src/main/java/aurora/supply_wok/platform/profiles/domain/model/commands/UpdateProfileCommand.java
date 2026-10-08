package aurora.supply_wok.platform.profiles.domain.model.commands;

import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;

/**
 * Command to create or update a profile for a role scope.
 */
public record UpdateProfileCommand(
        EProfileType profileType,
        String businessName,
        String firstName,
        String lastName,
        String email,
        String street,
        String district,
        String city,
        String country,
        String supportContact,
        boolean emailNotifications,
        boolean smsNotifications
) {
}
