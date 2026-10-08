package aurora.supply_wok.platform.iam.infrastructure.tokens.jwt.services;

import aurora.supply_wok.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TokenServiceImplTests {

    private TokenServiceImpl tokenService;

    @BeforeEach
    void setUp() {
        tokenService = new TokenServiceImpl();
        ReflectionTestUtils.setField(tokenService, "secret", "local-dev-secret-key-change-me-12345678901234567890");
        ReflectionTestUtils.setField(tokenService, "expirationDays", 7);
    }

    @Test
    void generateToken_whenEmailAndUserIdAndRolesProvided_containsClaimsAndValidates() {
        // Arrange
        String email = "restaurant@supplywok.com";
        Long userId = 42L;
        List<String> roles = List.of("RESTAURANT");

        // Act
        String token = tokenService.generateToken(email, userId, roles);

        // Assert
        assertThat(token).isNotBlank();
        assertThat(tokenService.validateToken(token)).isTrue();
        assertThat(tokenService.getEmailFromToken(token)).isEqualTo(email);
        assertThat(tokenService.getUserIdFromToken(token)).isEqualTo(42L);
        assertThat(tokenService.getRolesFromToken(token)).containsExactly("RESTAURANT");
    }

    @Test
    void generateToken_whenAuthenticationProvided_containsClaimsAndValidates() {
        // Arrange
        var userDetails = new UserDetailsImpl(
                99L,
                "supplier@supplywok.com",
                "secret",
                List.of(new SimpleGrantedAuthority("ROLE_SUPPLIER"))
        );
        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        // Act
        String token = tokenService.generateToken(auth);

        // Assert
        assertThat(token).isNotBlank();
        assertThat(tokenService.validateToken(token)).isTrue();
        assertThat(tokenService.getEmailFromToken(token)).isEqualTo("supplier@supplywok.com");
        assertThat(tokenService.getUserIdFromToken(token)).isEqualTo(99L);
        assertThat(tokenService.getRolesFromToken(token)).containsExactly("SUPPLIER");
    }

    @Test
    void validateToken_whenTokenIsMalformed_returnsFalse() {
        // Arrange
        String malformedToken = "not.a.valid.jwt.token";

        // Act
        boolean isValid = tokenService.validateToken(malformedToken);

        // Assert
        assertThat(isValid).isFalse();
    }

    @Test
    void getBearerTokenFrom_whenValidBearerHeaderPresent_extractsToken() {
        // Arrange
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Bearer token123");

        // Act
        String extracted = tokenService.getBearerTokenFrom(request);

        // Assert
        assertThat(extracted).isEqualTo("token123");
    }

    @Test
    void getBearerTokenFrom_whenHeaderMissingOrNotBearer_returnsNull() {
        // Arrange
        var request = mock(HttpServletRequest.class);
        when(request.getHeader("Authorization")).thenReturn("Basic xyz");

        // Act
        String extracted = tokenService.getBearerTokenFrom(request);

        // Assert
        assertThat(extracted).isNull();
    }
}
