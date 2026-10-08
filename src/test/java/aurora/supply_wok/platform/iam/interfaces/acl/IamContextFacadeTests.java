package aurora.supply_wok.platform.iam.interfaces.acl;

import aurora.supply_wok.platform.iam.application.commandservices.UserCommandService;
import aurora.supply_wok.platform.iam.application.queryservices.UserQueryService;
import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.commands.SignUpCommand;
import aurora.supply_wok.platform.iam.domain.model.queries.GetUserByEmailQuery;
import aurora.supply_wok.platform.iam.domain.model.queries.GetUserByIdQuery;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.shared.application.result.ApplicationError;
import aurora.supply_wok.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IamContextFacadeTests {

    @Mock
    private UserCommandService userCommandService;

    @Mock
    private UserQueryService userQueryService;

    @InjectMocks
    private IamContextFacade iamContextFacade;

    @Test
    void createUser_whenSignUpSucceeds_returnsUserId() {
        // Arrange
        var user = new User("alice@wok.pe", "pass", Roles.RESTAURANT);
        user.setId(88L);
        when(userCommandService.handle(any(SignUpCommand.class))).thenReturn(Result.success(user));

        // Act
        Long id = iamContextFacade.createUser("alice@wok.pe", "pass");

        // Assert
        assertThat(id).isEqualTo(88L);
        verify(userCommandService).handle(any(SignUpCommand.class));
    }

    @Test
    void createUser_whenSignUpFails_returnsZero() {
        // Arrange
        when(userCommandService.handle(any(SignUpCommand.class)))
                .thenReturn(Result.failure(ApplicationError.conflict("User", "Email already exists")));

        // Act
        Long id = iamContextFacade.createUser("alice@wok.pe", "pass");

        // Assert
        assertThat(id).isEqualTo(0L);
    }

    @Test
    void createUserWithRole_whenSignUpSucceeds_returnsUserId() {
        // Arrange
        var user = new User("sup@wok.pe", "pass", Roles.SUPPLIER);
        user.setId(99L);
        when(userCommandService.handle(any(SignUpCommand.class))).thenReturn(Result.success(user));

        // Act
        Long id = iamContextFacade.createUser("sup@wok.pe", "pass", "SUPPLIER");

        // Assert
        assertThat(id).isEqualTo(99L);
    }

    @Test
    void createUserWithRole_whenSignUpFails_returnsZero() {
        // Arrange
        when(userCommandService.handle(any(SignUpCommand.class)))
                .thenReturn(Result.failure(ApplicationError.validationError("role", "Invalid role")));

        // Act
        Long id = iamContextFacade.createUser("sup@wok.pe", "pass", "INVALID");

        // Assert
        assertThat(id).isEqualTo(0L);
    }

    @Test
    void fetchUserIdByEmail_whenUserFound_returnsUserId() {
        // Arrange
        var user = new User("alice@wok.pe", "pass");
        user.setId(77L);
        when(userQueryService.handle(any(GetUserByEmailQuery.class))).thenReturn(Optional.of(user));

        // Act
        Long id = iamContextFacade.fetchUserIdByEmail("alice@wok.pe");

        // Assert
        assertThat(id).isEqualTo(77L);
    }

    @Test
    void fetchUserIdByEmail_whenUserNotFound_returnsZero() {
        // Arrange
        when(userQueryService.handle(any(GetUserByEmailQuery.class))).thenReturn(Optional.empty());

        // Act
        Long id = iamContextFacade.fetchUserIdByEmail("notfound@wok.pe");

        // Assert
        assertThat(id).isEqualTo(0L);
    }

    @Test
    void fetchEmailByUserId_whenUserFound_returnsEmail() {
        // Arrange
        var user = new User("alice@wok.pe", "pass");
        user.setId(77L);
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.of(user));

        // Act
        String email = iamContextFacade.fetchEmailByUserId(77L);

        // Assert
        assertThat(email).isEqualTo("alice@wok.pe");
    }

    @Test
    void fetchEmailByUserId_whenUserNotFound_returnsEmptyString() {
        // Arrange
        when(userQueryService.handle(any(GetUserByIdQuery.class))).thenReturn(Optional.empty());

        // Act
        String email = iamContextFacade.fetchEmailByUserId(999L);

        // Assert
        assertThat(email).isEmpty();
    }
}
