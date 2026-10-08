package aurora.supply_wok.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Request payload for creating or updating a profile.
 */
@Schema(name = "UpdateProfileRequest", description = "Request payload for profile settings")
public record UpdateProfileResource(
        @Schema(description = "Business display name", example = "La Cucina Bella")
        String businessName,

        @Schema(description = "Primary contact first name", example = "Juan")
        String firstName,

        @Schema(description = "Primary contact last name", example = "Perez")
        String lastName,

        @Schema(description = "Primary contact email", example = "owner@supplywok.com")
        String email,

        @Schema(description = "Street address", example = "Av. Los Incas 123")
        String street,

        @Schema(description = "District", example = "Miraflores")
        String district,

        @Schema(description = "City", example = "Lima")
        String city,

        @Schema(description = "Country", example = "Peru")
        String country,

        @Schema(description = "Support phone or contact", example = "+(555) 123-4567")
        String supportContact,

        @Schema(description = "Whether email notifications are enabled", example = "true")
        boolean emailNotifications,

        @Schema(description = "Whether SMS notifications are enabled", example = "false")
        boolean smsNotifications
) {
}
