package aurora.supply_wok.platform.iam.infrastructure.authorization.sfs.pipeline;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import aurora.supply_wok.platform.iam.infrastructure.tokens.jwt.BearerTokenService;
import aurora.supply_wok.platform.shared.infrastructure.security.AuthenticatedUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BearerAuthorizationRequestFilterTests {

    @Mock
    private BearerTokenService tokenService;

    @Mock
    private UserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @InjectMocks
    private BearerAuthorizationRequestFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthenticatedUserPrincipalInSecurityContext() throws Exception {
        // Arrange
        String rawToken = "valid-token";
        String email = "restaurant@supplywok.pe";
        var user = new User(email, "hashed_pw", Roles.RESTAURANT);
        user.setId(7L);
        var userDetails = UserDetailsImpl.build(user);

        when(tokenService.getBearerTokenFrom(request)).thenReturn(rawToken);
        when(tokenService.validateToken(rawToken)).thenReturn(true);
        when(tokenService.getEmailFromToken(rawToken)).thenReturn(email);
        when(userDetailsService.loadUserByUsername(email)).thenReturn(userDetails);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        assertThat(authentication).isNotNull();
        assertThat(authentication.getPrincipal()).isInstanceOf(AuthenticatedUser.class);

        var principal = (AuthenticatedUser) authentication.getPrincipal();
        assertThat(principal.userId()).isEqualTo(7L);
        assertThat(principal.email()).isEqualTo(email);
        assertThat(principal.hasRole("RESTAURANT")).isTrue();

        verify(filterChain).doFilter(request, response);
    }

    @Test
    void doFilterInternal_invalidToken_doesNotSetSecurityContext() throws Exception {
        // Arrange
        String rawToken = "invalid-token";
        when(tokenService.getBearerTokenFrom(request)).thenReturn(rawToken);
        when(tokenService.validateToken(rawToken)).thenReturn(false);

        // Act
        filter.doFilterInternal(request, response, filterChain);

        // Assert
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(userDetailsService);
    }
}
