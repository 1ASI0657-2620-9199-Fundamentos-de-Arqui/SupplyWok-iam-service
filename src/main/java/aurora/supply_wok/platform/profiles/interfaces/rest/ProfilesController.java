package aurora.supply_wok.platform.profiles.interfaces.rest;

import aurora.supply_wok.platform.profiles.application.commandservices.ProfileCommandService;
import aurora.supply_wok.platform.profiles.application.queryservices.ProfileQueryService;
import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeAndEmailQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfilesByTypeQuery;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.interfaces.rest.resources.ProfileResource;
import aurora.supply_wok.platform.profiles.interfaces.rest.resources.UpdateProfileResource;
import aurora.supply_wok.platform.profiles.interfaces.rest.transform.ProfileResourceFromEntityAssembler;
import aurora.supply_wok.platform.profiles.interfaces.rest.transform.UpdateProfileCommandFromResourceAssembler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

    public ProfilesController(ProfileCommandService profileCommandService, ProfileQueryService profileQueryService) {
        this.profileCommandService = profileCommandService;
        this.profileQueryService = profileQueryService;
    }

    @GetMapping("/{profileType}")
    @Operation(summary = "Get profile by type", description = "Retrieves restaurant or supplier profile settings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid profile type")
    })
    public ResponseEntity<ProfileResource> getProfileByType(@PathVariable String profileType) {
        try {
            var parsedType = EProfileType.fromPath(profileType);
            var profile = profileQueryService.handle(new GetProfileByTypeQuery(parsedType))
                    .orElseGet(() -> Profile.defaultFor(parsedType));
            return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{profileType}/accounts")
    @Operation(summary = "Get profiles by type", description = "Retrieves all account profiles for a profile type.")
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
    @Operation(summary = "Get profile by account email", description = "Retrieves one profile for a role scope and account email.")
    public ResponseEntity<ProfileResource> getProfileByTypeAndEmail(@PathVariable String profileType,
                                                                    @RequestParam String email) {
        try {
            var parsedType = EProfileType.fromPath(profileType);
            var profile = profileQueryService.handle(new GetProfileByTypeAndEmailQuery(parsedType, email))
                    .or(() -> {
                        var profiles = profileQueryService.handle(new GetProfilesByTypeQuery(parsedType));
                        return profiles.size() == 1 ? java.util.Optional.of(profiles.get(0)) : java.util.Optional.empty();
                    })
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
    @Operation(summary = "Update profile", description = "Creates or updates restaurant or supplier profile settings.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Profile updated successfully",
                    content = @Content(schema = @Schema(implementation = ProfileResource.class))),
            @ApiResponse(responseCode = "400", description = "Invalid request data")
    })
    public ResponseEntity<ProfileResource> updateProfile(@PathVariable String profileType,
                                                         @Valid @RequestBody UpdateProfileResource resource) {
        try {
            var parsedType = EProfileType.fromPath(profileType);
            var command = UpdateProfileCommandFromResourceAssembler.toCommandFromResource(parsedType, resource);
            var profile = profileCommandService.handle(command);
            return ResponseEntity.ok(ProfileResourceFromEntityAssembler.toResourceFromEntity(profile));
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{profileType}/accounts/by-email")
    @Operation(summary = "Update profile by account email", description = "Creates or updates account profile settings.")
    public ResponseEntity<ProfileResource> updateProfileByEmail(@PathVariable String profileType,
                                                                @RequestParam String email,
                                                                @Valid @RequestBody UpdateProfileResource resource) {
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
