package aurora.supply_wok.platform.iam.acceptance;

import aurora.supply_wok.platform.iam.application.internal.commandservices.UserCommandServiceImpl;
import aurora.supply_wok.platform.iam.application.internal.outboundservices.hashing.HashingService;
import aurora.supply_wok.platform.iam.application.internal.outboundservices.tokens.TokenService;
import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.commands.SignUpCommand;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import aurora.supply_wok.platform.shared.application.result.ApplicationError;
import aurora.supply_wok.platform.shared.application.result.Result;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SignUpSteps {

    private UserRepository userRepository;
    private HashingService hashingService;
    private TokenService tokenService;
    private UserCommandServiceImpl userCommandService;

    private Map<String, User> userStorage;
    private Result<User, ApplicationError> result;

    @Before
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        hashingService = Mockito.mock(HashingService.class);
        tokenService = Mockito.mock(TokenService.class);
        userCommandService = new UserCommandServiceImpl(userRepository, hashingService, tokenService);
        userStorage = new HashMap<>();

        when(hashingService.encode(any())).thenAnswer(inv -> "encoded_" + inv.getArgument(0));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            userStorage.put(u.getEmail(), u);
            when(userRepository.findByEmail(u.getEmail())).thenReturn(Optional.of(u));
            return u;
        });
    }

    @Given("the email {string} is available for registration")
    public void emailIsAvailableForRegistration(String email) {
        when(userRepository.existsByEmail(email)).thenReturn(false);
    }

    @Given("any registration request")
    public void anyRegistrationRequest() {
        when(userRepository.existsByEmail(any())).thenReturn(false);
    }

    @Given("a user already exists with email {string}")
    public void userAlreadyExistsWithEmail(String email) {
        var existingUser = new User(email, "hashed_secret", Roles.RESTAURANT);
        userStorage.put(email, existingUser);
        when(userRepository.existsByEmail(email)).thenReturn(true);
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(existingUser));
    }

    @When("the user signs up with email {string}, password {string} and role {string}")
    public void userSignsUpWithEmailPasswordAndRole(String email, String password, String role) {
        var command = new SignUpCommand(email, password, role);
        result = userCommandService.handle(command);
    }

    @Then("the registration succeeds")
    public void registrationSucceeds() {
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
    }

    @And("the created user has email {string} and role {string}")
    public void createdUserHasEmailAndRole(String email, String role) {
        assertThat(result.value()).isNotNull();
        assertThat(result.value().getEmail()).isEqualTo(email);
        assertThat(result.value().getRole()).isEqualTo(Roles.valueOf(role));
    }

    @Then("the registration fails with validation error {string}")
    public void registrationFailsWithValidationError(String errorMessage) {
        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(result.error().details()).isEqualTo(errorMessage);
    }

    @Then("the registration fails with a conflict error")
    public void registrationFailsWithAConflictError() {
        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("USER_CONFLICT");
    }
}
