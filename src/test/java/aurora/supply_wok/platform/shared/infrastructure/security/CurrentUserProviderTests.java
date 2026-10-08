package aurora.supply_wok.platform.shared.infrastructure.security;

import aurora.supply_wok.platform.iam.infrastructure.authorization.sfs.model.UserDetailsImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CurrentUserProviderTests {

    private CurrentUserProvider currentUserProvider;

    @BeforeEach
    void setUp() {
        currentUserProvider = new CurrentUserProvider();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void get_authenticatedUserPrincipalPresent_returnsAuthenticatedUser() {
        // Arrange
        var authenticatedUser = new AuthenticatedUser(42L, "test@supplywok.pe", List.of("RESTAURANT"));
        var auth = new UsernamePasswordAuthenticationToken(authenticatedUser, null, List.of(new SimpleGrantedAuthority("ROLE_RESTAURANT")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        var result = currentUserProvider.get();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(42L);
        assertThat(result.email()).isEqualTo("test@supplywok.pe");
        assertThat(result.hasRole("RESTAURANT")).isTrue();
    }

    @Test
    void get_userDetailsImplPrincipalPresent_convertsToAuthenticatedUser() {
        // Arrange
        var userDetails = new UserDetailsImpl(99L, "owner@restaurant.pe", "secret", List.of(new SimpleGrantedAuthority("ROLE_RESTAURANT")));
        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        var result = currentUserProvider.get();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.userId()).isEqualTo(99L);
        assertThat(result.email()).isEqualTo("owner@restaurant.pe");
        assertThat(result.hasRole("RESTAURANT")).isTrue();
    }

    @Test
    void get_noAuthentication_throwsAuthenticationCredentialsNotFoundException() {
        // Arrange
        SecurityContextHolder.clearContext();

        // Act & Assert
        assertThatThrownBy(() -> currentUserProvider.get())
                .isInstanceOf(AuthenticationCredentialsNotFoundException.class)
                .hasMessageContaining("User is not authenticated");
    }

    @Test
    void userId_authenticatedUserPresent_returnsUserId() {
        // Arrange
        var authenticatedUser = new AuthenticatedUser(10L, "admin@supplywok.pe", List.of("ADMIN"));
        var auth = new UsernamePasswordAuthenticationToken(authenticatedUser, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        var userId = currentUserProvider.userId();

        // Assert
        assertThat(userId).isEqualTo(10L);
    }

    @Test
    void email_authenticatedUserPresent_returnsEmail() {
        // Arrange
        var authenticatedUser = new AuthenticatedUser(10L, "admin@supplywok.pe", List.of("ADMIN"));
        var auth = new UsernamePasswordAuthenticationToken(authenticatedUser, null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
        SecurityContextHolder.getContext().setAuthentication(auth);

        // Act
        var email = currentUserProvider.email();

        // Assert
        assertThat(email).isEqualTo("admin@supplywok.pe");
    }
}
