package aurora.supply_wok.platform.iam.application.internal.commandservices;

import aurora.supply_wok.platform.iam.application.internal.outboundservices.hashing.HashingService;
import aurora.supply_wok.platform.iam.application.internal.outboundservices.tokens.TokenService;
import aurora.supply_wok.platform.iam.application.commandservices.UserCommandService;
import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.commands.SignInCommand;
import aurora.supply_wok.platform.iam.domain.model.commands.SignUpCommand;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import aurora.supply_wok.platform.shared.application.result.ApplicationError;
import aurora.supply_wok.platform.shared.application.result.Result;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.springframework.stereotype.Service;

/**
 * User command service implementation.
 */
@Service
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final HashingService hashingService;
    private final TokenService tokenService;

    public UserCommandServiceImpl(
            UserRepository userRepository,
            HashingService hashingService,
            TokenService tokenService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
        this.tokenService = tokenService;
    }

    @Override
    public Result<ImmutablePair<User, String>, ApplicationError> handle(SignInCommand command) {
        var user = userRepository.findByEmail(command.email());
        if (user.isEmpty()) {
            return Result.failure(ApplicationError.notFound("User", command.email()));
        }
        if (!hashingService.matches(command.password(), user.get().getPassword())) {
            return Result.failure(ApplicationError.validationError("credentials", "Invalid email or password"));
        }
        var userEntity = user.get();
        var roles = userEntity.getRole() != null ? java.util.List.of(userEntity.getRole().name()) : java.util.List.<String>of();
        var token = tokenService.generateToken(userEntity.getEmail(), userEntity.getId(), roles);
        return Result.success(ImmutablePair.of(userEntity, token));
    }

    @Override
    public Result<User, ApplicationError> handle(SignUpCommand command) {
        if (userRepository.existsByEmail(command.email())) {
            return Result.failure(ApplicationError.conflict("User", "Email already exists"));
        }

        Roles role;
        try {
            role = Roles.valueOf(command.role().toUpperCase());
        } catch (IllegalArgumentException | NullPointerException e) {
            return Result.failure(ApplicationError.validationError("role", "Invalid role name"));
        }

        if (role == Roles.ADMIN) {
            return Result.failure(ApplicationError.validationError("role", "Role not allowed for sign-up"));
        }

        var user = new User(command.email(), hashingService.encode(command.password()), role);
        userRepository.save(user);
        return userRepository.findByEmail(command.email())
                .<Result<User, ApplicationError>>map(Result::success)
                .orElseGet(() -> Result.failure(ApplicationError.unexpected("sign-up", "Created user could not be reloaded")));
    }
}
