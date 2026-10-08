package aurora.supply_wok.platform.shared.infrastructure.security;

import java.util.List;

/**
 * Represents the authenticated user principal.
 *
 * @param userId the user identifier
 * @param email  the user email
 * @param roles  the list of roles assigned to the user
 */
public record AuthenticatedUser(Long userId, String email, List<String> roles) {

    public AuthenticatedUser {
        if (roles == null) {
            roles = List.of();
        }
    }

    /**
     * Checks if the user has a specific role.
     *
     * @param r the role name, optionally prefixed with ROLE_
     * @return true if the user has the specified role, false otherwise
     */
    public boolean hasRole(String r) {
        if (r == null || roles == null) {
            return false;
        }
        String target = r.startsWith("ROLE_") ? r.substring(5) : r;
        return roles.stream().anyMatch(role -> {
            String normalized = role.startsWith("ROLE_") ? role.substring(5) : role;
            return normalized.equalsIgnoreCase(target);
        });
    }
}
