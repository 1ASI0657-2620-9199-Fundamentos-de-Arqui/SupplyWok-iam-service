package aurora.supply_wok.platform.profiles.interfaces.rest;

import aurora.supply_wok.platform.profiles.application.commandservices.ProfileCommandService;
import aurora.supply_wok.platform.profiles.application.queryservices.ProfileQueryService;
import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeAndEmailQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfilesByTypeQuery;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.interfaces.rest.resources.ProfileResource;
import aurora.supply_wok.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import aurora.supply_wok.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import aurora.supply_wok.platform.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import aurora.supply_wok.platform.shared.infrastructure.security.CurrentUserProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST controller that exposes profile settings endpoints.
 */
@RestController
@RequestMapping(value = "/api/v1/profiles", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = "Profiles", description = "Restaurant and supplier profile settings endpoints")
public class ProfilesController {

    private final ProfileCommandService profileCommandService;
    private final ProfileQueryService profileQueryService;
    private final CurrentUserProvider currentUserProvider;

    public ProfilesController(ProfileCommandService profileCommandService,
                              ProfileQueryService profileQueryService,
                              CurrentUserProvider currentUserProvider) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
        this.currentUserProvider = currentUserProvider;
    }

    @GetMapping("/{profileType}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get profile by type", description = "Retrieves restaurant or supplier profile settings for the authenticated caller.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid profile type"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid")
    })
    public ResponseEntity<ProfileResource> getProfileByType(@PathVariable String profileType) {
        try {
            var parsedType = EProfileType.fromPath(profileType);
            var callerEmail = currentUserProvider.email();
            var profile = profileQueryService.handle(new GetProfileByTypeAndEmailQuery(parsedType, callerEmail))
                    .orElseGet(() -> {
                        var defaultProfile = Profile.defaultFor(parsedType);
                        defaultProfile.update(UpdateProfileCommandFromResourceAssembler.toCommandFromResource(
                                parsedType,
                                new UpdateProfileResource(defaultProfile.getBusinessName(), "", "", callerEmail, "", "", "", "", "", true, false)
                        ));
                        return defaultProfile;
                    });
            return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{profileType}/accounts")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get profiles by type", description = "Retrieves all account profiles for a profile type directory.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profiles retrieved successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid profile type"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid")
    })
    public ResponseEntity<List<ProfileResource>> getProfilesByType(@PathVariable String profileType) {
        try {
            var parsedType = EProfileType.fromPath(profileType);
            var profiles = profileQueryService.handle(new GetProfilesByTypeQuery(parsedType)).stream()
                    .map(ProfileResourceFromEntityAssembler::toResourceFromEntity)
                    .toList();
            return ResponseEntity.ok(profiles);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{profileType}/accounts/by-email")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get profile by account email", description = "Retrieves one profile for a role scope and account email.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid profile type"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller email does not match requested email")
    })
    public ResponseEntity<ProfileResource> getProfileByTypeAndEmail(@PathVariable String profileType,
                                                                    @RequestParam String email) {
        var currentUser = currentUserProvider.get();
        if (!currentUser.hasRole("ADMIN") && !currentUser.email().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("User is not authorized to access profile for this email");
        }
        try {
            var parsedType = EProfileType.fromPath(profileType);
            var profile = profileQueryService.handle(new GetProfileByTypeAndEmailQuery(parsedType, email))
                    .orElseGet(() -> {
                        var defaultProfile = Profile.defaultFor(parsedType);
                        defaultProfile.update(UpdateProfileCommandFromResourceAssembler.toCommandFromResource(
                                parsedType,
                                new UpdateProfileResource(defaultProfile.getBusinessName(), "", "", email, "", "", "", "", "", true, false)
                        ));
                        return defaultProfile;
                    });
            return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{profileType}")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update profile", description = "Creates or updates restaurant or supplier profile settings for the authenticated caller.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid")
    })
    public ResponseEntity<ProfileResource> updateProfile(@PathVariable String profileType,
                                                         @Valid @RequestBody UpdateProfileResource resource) {
        try {
            var parsedType = EProfileType.fromPath(profileType);
            var callerEmail = currentUserProvider.email();
            var resourceWithCallerEmail = new UpdateProfileResource(
                    resource.businessName(),
                    resource.firstName(),
                    resource.lastName(),
                    callerEmail,
                    resource.street(),
                    resource.district(),
                    resource.city(),
                    resource.country(),
                    resource.supportContact(),
                    resource.emailNotifications(),
                    resource.smsNotifications()
            );
            var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(parsedType, resourceWithCallerEmail);
            var profile = profileCommandService.handle(command);
            return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{profileType}/accounts/by-email")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Update profile by account email", description = "Creates or updates account profile settings.", security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - JWT token required or invalid"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Caller email does not match requested email")
    })
    public ResponseEntity<ProfileResource> updateProfileByEmail(@PathVariable String profileType,
                                                                @RequestParam String email,
                                                                @Valid @RequestBody UpdateProfileResource resource) {
        var currentUser = currentUserProvider.get();
        if (!currentUser.hasRole("ADMIN") && !currentUser.email().equalsIgnoreCase(email)) {
            throw new AccessDeniedException("User is not authorized to modify profile for this email");
        }
        try {
            var parsedType = EProfileType.fromPath(profileType);
            var resourceWithAccountEmail = new UpdateProfileResource(
                    resource.businessName(),
                    resource.firstName(),
                    resource.lastName(),
                    email,
                    resource.street(),
                    resource.district(),
                    resource.city(),
                    resource.country(),
                    resource.supportContact(),
                    resource.emailNotifications(),
                    resource.smsNotifications()
            );
            var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(parsedType, resourceWithAccountEmail);
            var profile = profileCommandService.handle(command);
            return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }
}
