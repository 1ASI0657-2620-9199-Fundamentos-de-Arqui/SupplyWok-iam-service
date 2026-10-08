package aurora.supply_wok.platform.iam.infrastructure.authorization.sfs.services;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    @Test
    void loadUserByUsername_whenUserExists_returnsUserDetails() {
        // Arrange
        var user = new User("admin@wok.pe", "hashedPass", Roles.ADMIN);
        user.setId(1L);
        when(userRepository.findByEmail("admin@wok.pe")).thenReturn(Optional.of(user));

        // Act
        var userDetails = userDetailsService.loadUserByUsername("admin@wok.pe");

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo("admin@wok.pe");
        assertThat(userDetails.getPassword()).isEqualTo("hashedPass");
        assertThat(userDetails.getAuthorities()).extracting("authority").containsExactly("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_whenUserNotFound_throwsUsernameNotFoundException() {
        // Arrange
        when(userRepository.findByEmail("missing@wok.pe")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("missing@wok.pe"));
    }
}
