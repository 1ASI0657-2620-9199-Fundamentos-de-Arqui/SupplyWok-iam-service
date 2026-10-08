package aurora.supply_wok.platform.iam.application.internal.commandservices;

import aurora.supply_wok.platform.iam.application.internal.outboundservices.hashing.HashingService;
import aurora.supply_wok.platform.iam.application.internal.outboundservices.tokens.TokenService;
import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.commands.SignInCommand;
import aurora.supply_wok.platform.iam.domain.model.commands.SignUpCommand;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserCommandServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @Mock
    private HashingService hashingService;

    @Mock
    private TokenService tokenService;

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Test
    void signUp_whenRoleIsAdmin_failsWithRoleNotAllowed() {
        // Arrange
        var command = new SignUpCommand("admin@supplywok.com", "SecretPass123!", "ADMIN");
        when(userRepository.existsByEmail("admin@supplywok.com")).thenReturn(false);
        lenient().when(hashingService.encode("SecretPass123!")).thenReturn("hashedPassword");
        lenient().when(userRepository.findByEmail("admin@supplywok.com"))
                .thenReturn(Optional.of(new User("admin@supplywok.com", "hashedPassword", Roles.ADMIN)));

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().details()).isEqualTo("Role not allowed for sign-up");
        verify(userRepository, never()).save(any());
    }

    @Test
    void signUp_whenRoleIsRestaurant_returnsSuccess() {
        // Arrange
        var command = new SignUpCommand("restaurant@supplywok.com", "SecretPass123!", "RESTAURANT");
        when(userRepository.existsByEmail("restaurant@supplywok.com")).thenReturn(false);
        when(hashingService.encode("SecretPass123!")).thenReturn("hashedPassword");
        var savedUser = new User("restaurant@supplywok.com", "hashedPassword", Roles.RESTAURANT);
        when(userRepository.findByEmail("restaurant@supplywok.com")).thenReturn(Optional.of(savedUser));

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value().getEmail()).isEqualTo("restaurant@supplywok.com");
        assertThat(result.value().getRole()).isEqualTo(Roles.RESTAURANT);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signUp_whenEmailAlreadyExists_returnsConflictError() {
        // Arrange
        var command = new SignUpCommand("existing@supplywok.com", "SecretPass123!", "RESTAURANT");
        when(userRepository.existsByEmail("existing@supplywok.com")).thenReturn(true);

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("USER_CONFLICT");
        assertThat(result.error().details()).isEqualTo("Email already exists");
        verify(userRepository, never()).save(any());
    }

    @Test
    void signUp_whenRoleIsInvalid_returnsValidationError() {
        // Arrange
        var command = new SignUpCommand("new@supplywok.com", "SecretPass123!", "NON_EXISTENT_ROLE");
        when(userRepository.existsByEmail("new@supplywok.com")).thenReturn(false);

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(result.error().details()).isEqualTo("Invalid role name");
        verify(userRepository, never()).save(any());
    }

    @Test
    void signUp_whenCreatedUserCannotBeReloaded_returnsUnexpectedError() {
        // Arrange
        var command = new SignUpCommand("supplier@supplywok.com", "SecretPass123!", "SUPPLIER");
        when(userRepository.existsByEmail("supplier@supplywok.com")).thenReturn(false);
        when(hashingService.encode("SecretPass123!")).thenReturn("hashedPassword");
        when(userRepository.findByEmail("supplier@supplywok.com")).thenReturn(Optional.empty());

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("UNEXPECTED_ERROR");
        assertThat(result.error().details()).isEqualTo("Created user could not be reloaded");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void signIn_whenValidCredentials_returnsAuthenticatedUserAndToken() {
        // Arrange
        var command = new SignInCommand("restaurant@supplywok.com", "SecretPass123!");
        var user = new User("restaurant@supplywok.com", "hashedPassword", Roles.RESTAURANT);
        user.setId(77L);
        when(userRepository.findByEmail("restaurant@supplywok.com")).thenReturn(Optional.of(user));
        when(hashingService.matches("SecretPass123!", "hashedPassword")).thenReturn(true);
        when(tokenService.generateToken("restaurant@supplywok.com", 77L, List.of("RESTAURANT"))).thenReturn("jwt.token.here");

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value().getRight()).isEqualTo("jwt.token.here");
        assertThat(result.value().getLeft().getId()).isEqualTo(77L);
        verify(tokenService).generateToken("restaurant@supplywok.com", 77L, List.of("RESTAURANT"));
    }

    @Test
    void signIn_whenUserNotFound_returnsNotFoundError() {
        // Arrange
        var command = new SignInCommand("missing@supplywok.com", "SecretPass123!");
        when(userRepository.findByEmail("missing@supplywok.com")).thenReturn(Optional.empty());

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("USER_NOT_FOUND");
        assertThat(result.error().message()).contains("missing@supplywok.com");
    }

    @Test
    void signIn_whenPasswordDoesNotMatch_returnsValidationError() {
        // Arrange
        var command = new SignInCommand("restaurant@supplywok.com", "WrongPassword!");
        var user = new User("restaurant@supplywok.com", "hashedPassword", Roles.RESTAURANT);
        when(userRepository.findByEmail("restaurant@supplywok.com")).thenReturn(Optional.of(user));
        when(hashingService.matches("WrongPassword!", "hashedPassword")).thenReturn(false);

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(result.error().details()).isEqualTo("Invalid email or password");
    }

    @Test
    void signIn_whenUserRoleIsNull_generatesTokenWithEmptyRoles() {
        // Arrange
        var command = new SignInCommand("norole@supplywok.com", "SecretPass123!");
        var user = new User("norole@supplywok.com", "hashedPassword");
        user.setId(88L);
        when(userRepository.findByEmail("norole@supplywok.com")).thenReturn(Optional.of(user));
        when(hashingService.matches("SecretPass123!", "hashedPassword")).thenReturn(true);
        when(tokenService.generateToken("norole@supplywok.com", 88L, List.of())).thenReturn("jwt.token.empty.roles");

        // Act
        var result = userCommandService.handle(command);

        // Assert
        assertThat(result.isSuccess()).isTrue();
        assertThat(result.value().getRight()).isEqualTo("jwt.token.empty.roles");
        verify(tokenService).generateToken("norole@supplywok.com", 88L, List.of());
    }
}
