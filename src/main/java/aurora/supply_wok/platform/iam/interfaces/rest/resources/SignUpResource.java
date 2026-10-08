package aurora.supply_wok.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Resource received to register a new IAM user.
 */
@Schema(
    name = "SignUpRequest",
    description = "User sign-up request with email, password, and roles",
    example = "{\"email\": \"john.doe@example.com\", \"password\": \"SecurePass123!\", \"role\": \"Restaurant\"}"
)
public record SignUpResource(
    @Schema(
        description = "User email",
        example = "john.doe@example.com",
        maxLength = 120
    )
    String email,

    @Schema(
        description = "User password (minimum 8 characters)",
        example = "SecurePass123!",
        minLength = 8,
        maxLength = 255
    )
    String password,

    @Schema(
        description = "Role to assign to the user",
        example = "Restaurant"
    )
    String role
) {
}
