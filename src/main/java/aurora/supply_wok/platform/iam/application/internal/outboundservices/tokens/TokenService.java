package aurora.supply_wok.platform.iam.application.internal.outboundservices.tokens;

import java.util.List;

/**
 * Outbound port for bearer token issuance and validation used by IAM commands and queries.
 */
public interface TokenService {

    /**
     * Generates a token with subject, userId, and roles claims.
     *
     * @param email principal email (subject)
     * @param userId user identifier
     * @param roles list of role names
     * @return signed token value
     */
    String generateToken(String email, Long userId, List<String> roles);

    /**
     * Generates a token for an email principal.
     *
     * @param email principal email
     * @return signed token value
     */
    String generateToken(String email);

    /**
     * Extracts the email from a token.
     *
     * @param token token value
     * @return email embedded in the token
     */
    String getEmailFromToken(String token);

    /**
     * Extracts the userId from a token.
     *
     * @param token token value
     * @return userId embedded in the token, or null if absent
     */
    Long getUserIdFromToken(String token);

    /**
     * Extracts the roles from a token.
     *
     * @param token token value
     * @return list of role names embedded in the token
     */
    List<String> getRolesFromToken(String token);

    /**
     * Validates a token.
     *
     * @param token token value
     * @return {@code true} when token is valid; otherwise {@code false}
     */
    boolean validateToken(String token);
}
