package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Provider component to retrieve the currently authenticated user from the SecurityContext.
 */
@Component
public class CurrentUserProvider {

    /**
     * Retrieves the authenticated user principal.
     *
     * @return the authenticated user
     * @throws AuthenticationCredentialsNotFoundException if no user is authenticated
     */
    public AuthenticatedUser get() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AuthenticationCredentialsNotFoundException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof AuthenticatedUser authenticatedUser) {
            return authenticatedUser;
        }

        if (principal instanceof UserDetails userDetails) {
            Long id = (userDetails instanceof UserDetailsImpl udi) ? udi.getId() : null;
            List<String> roles = userDetails.getAuthorities() != null
                    ? userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(auth -> auth.startsWith("ROLE_") ? auth.substring(5) : auth)
                    .toList()
                    : List.of();
            return new AuthenticatedUser(id, userDetails.getUsername(), roles);
        }

        throw new AuthenticationCredentialsNotFoundException("No authenticated user principal found");
    }

    /**
     * Returns the identifier of the authenticated user.
     *
     * @return the user id
     */
    public Long userId() {
        return get().userId();
    }

    /**
     * Returns the email of the authenticated user.
     *
     * @return the user email
     */
    public String email() {
        return get().email();
    }
}
