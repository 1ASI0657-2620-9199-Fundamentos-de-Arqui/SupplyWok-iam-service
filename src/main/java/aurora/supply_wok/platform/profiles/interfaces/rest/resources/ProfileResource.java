package aurora.supply_wok.platform.profiles.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Profile response resource.
 */
@Schema(name = "ProfileResponse", description = "Restaurant or supplier account profile")
public record ProfileResource(
        @Schema(description = "Profile unique identifier", example = "1")
        Long id,

        @Schema(description = "Profile role scope", example = "RESTAURANT")
        String profileType,

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
