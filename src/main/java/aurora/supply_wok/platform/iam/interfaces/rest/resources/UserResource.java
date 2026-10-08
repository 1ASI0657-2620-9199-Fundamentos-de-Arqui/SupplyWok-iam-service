package aurora.supply_wok.platform.iam.interfaces.rest.resources;

import io.swagger.v3.oas.annotations.media.Schema;


/**
 * Resource representing an IAM user returned by the REST API.
 */
@Schema(
    name = "UserResponse",
    description = "User information response",
    example = "{\"id\": 1, \"email\": \"john.doe@example.com\", \"role\": \"Restaurant\"}"
)
public record UserResource(
    @Schema(description = "User unique identifier", example = "1")
    Long id,

    @Schema(description = "User email", example = "john.doe@example.com")
    String email,

    @Schema(description = "User assigned role", example = "Restaurant")
    String role
) {
}
