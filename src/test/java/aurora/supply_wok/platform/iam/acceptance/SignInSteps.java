package aurora.supply_wok.platform.iam.acceptance;

import aurora.supply_wok.platform.iam.application.internal.commandservices.UserCommandServiceImpl;
import aurora.supply_wok.platform.iam.application.internal.outboundservices.hashing.HashingService;
import aurora.supply_wok.platform.iam.application.internal.outboundservices.tokens.TokenService;
import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.commands.SignInCommand;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import aurora.supply_wok.platform.shared.application.result.ApplicationError;
import aurora.supply_wok.platform.shared.application.result.Result;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.mockito.Mockito;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class SignInSteps {

    private UserRepository userRepository;
    private HashingService hashingService;
    private TokenService tokenService;
    private UserCommandServiceImpl userCommandService;

    private Result<ImmutablePair<User, String>, ApplicationError> result;

    @Before
    public void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        hashingService = Mockito.mock(HashingService.class);
        tokenService = Mockito.mock(TokenService.class);
        userCommandService = new UserCommandServiceImpl(userRepository, hashingService, tokenService);
    }

    @Given("an existing user with email {string}, password {string} and role {string}")
    public void anExistingUserWithEmailPasswordAndRole(String email, String rawPassword, String role) {
        var user = new User(email, "hashed_" + rawPassword, Roles.valueOf(role));
        user.setId(10L);

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(hashingService.matches(rawPassword, "hashed_" + rawPassword)).thenReturn(true);
        when(tokenService.generateToken(any(), any(), any())).thenReturn("mocked.jwt.token");
    }

    @Given("no user is registered with email {string}")
    public void noUserIsRegisteredWithEmail(String email) {
        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
    }

    @When("the user signs in with email {string} and password {string}")
    public void userSignsInWithEmailAndPassword(String email, String password) {
        var command = new SignInCommand(email, password);
        result = userCommandService.handle(command);
    }

    @Then("the sign-in succeeds")
    public void signInSucceeds() {
        assertThat(result).isNotNull();
        assertThat(result.isSuccess()).isTrue();
    }

    @And("a valid access token is returned")
    public void validAccessTokenIsReturned() {
        assertThat(result.value()).isNotNull();
        assertThat(result.value().getRight()).isNotBlank();
    }

    @Then("the sign-in fails with invalid credentials error")
    public void signInFailsWithInvalidCredentialsError() {
        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(result.error().details()).isEqualTo("Invalid email or password");
    }

    @Then("the sign-in fails because the user was not found")
    public void signInFailsBecauseUserWasNotFound() {
        assertThat(result).isNotNull();
        assertThat(result.isFailure()).isTrue();
        assertThat(result.error().code()).isEqualTo("USER_NOT_FOUND");
    }
}
