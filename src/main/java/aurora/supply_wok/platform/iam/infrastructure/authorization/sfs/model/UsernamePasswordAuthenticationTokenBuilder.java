package aurora.supply_wok.platform.iam.infrastructure.authorization.sfs.model;

import aurora.supply_wok.platform.shared.infrastructure.security.AuthenticatedUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;

import java.util.Collection;

/**
 * This class is used to build the UsernamePasswordAuthenticationToken object
 * that is used to authenticate the user.
 */
public class UsernamePasswordAuthenticationTokenBuilder {

    /**
     * This method is responsible for building the UsernamePasswordAuthenticationToken object from UserDetails.
     * @param principal The user details.
     * @param request The HTTP request.
     * @return The UsernamePasswordAuthenticationToken object.
     * @see UsernamePasswordAuthenticationToken
     * @see UserDetails
     */
    public static UsernamePasswordAuthenticationToken build(UserDetails principal, HttpServletRequest request) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }

    /**
     * Builds the UsernamePasswordAuthenticationToken object with an AuthenticatedUser principal.
     * @param principal The authenticated user.
     * @param authorities The granted authorities.
     * @param request The HTTP request.
     * @return The UsernamePasswordAuthenticationToken object.
     */
    public static UsernamePasswordAuthenticationToken build(AuthenticatedUser principal, Collection<? extends GrantedAuthority> authorities, HttpServletRequest request) {
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        usernamePasswordAuthenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        return usernamePasswordAuthenticationToken;
    }
}
