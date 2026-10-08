package aurora.supply_wok.platform.iam.interfaces.rest.transform;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.interfaces.rest.resources.AuthenticatedUserResource;

/**
 * Assembler that translates IAM authentication results into {@link AuthenticatedUserResource}.
 */
public class AuthenticatedUserResourceFromEntityAssembler {
    /**
     * Creates a resource from the authenticated {@link User} aggregate and issued bearer token.
     *
     * @param user authenticated user aggregate
     * @param token generated bearer token
     * @return resource used by the authentication endpoint response
     */
    public static AuthenticatedUserResource toResourceFromEntity(User user, String token) {
        java.util.List<String> roles = user.getRole() != null ? java.util.List.of(user.getRole().name()) : java.util.List.<String>of();
        String roleStr = user.getRole() != null ? user.getRole().name() : null;
        return new AuthenticatedUserResource(user.getId(), user.getEmail(), roles, roleStr, token);
    }
}
