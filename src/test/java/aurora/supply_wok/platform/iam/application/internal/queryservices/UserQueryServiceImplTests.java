package aurora.supply_wok.platform.iam.application.internal.queryservices;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.queries.GetAllUsersQuery;
import aurora.supply_wok.platform.iam.domain.model.queries.GetUserByEmailQuery;
import aurora.supply_wok.platform.iam.domain.model.queries.GetUserByIdQuery;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserQueryServiceImplTests {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    @Test
    void handle_whenGetAllUsersQuery_delegatesToRepositoryFindAll() {
        // Arrange
        var expectedUsers = List.of(new User("u1@wok.com", "p1"), new User("u2@wok.com", "p2"));
        when(userRepository.findAll()).thenReturn(expectedUsers);
        var query = new GetAllUsersQuery();

        // Act
        var result = userQueryService.handle(query);

        // Assert
        assertThat(result).isEqualTo(expectedUsers);
        verify(userRepository).findAll();
    }

    @Test
    void handle_whenGetUserByIdQuery_delegatesToRepositoryFindById() {
        // Arrange
        Long userId = 10L;
        var user = new User("u1@wok.com", "p1");
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        var query = new GetUserByIdQuery(userId);

        // Act
        var result = userQueryService.handle(query);

        // Assert
        assertThat(result).contains(user);
        verify(userRepository).findById(userId);
    }

    @Test
    void handle_whenGetUserByEmailQuery_delegatesToRepositoryFindByEmail() {
        // Arrange
        String email = "u1@wok.com";
        var user = new User(email, "p1");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        var query = new GetUserByEmailQuery(email);

        // Act
        var result = userQueryService.handle(query);

        // Assert
        assertThat(result).contains(user);
        verify(userRepository).findByEmail(email);
    }
}
