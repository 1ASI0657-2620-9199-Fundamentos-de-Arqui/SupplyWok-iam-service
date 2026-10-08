package aurora.supply_wok.platform.iam.application.queryservices;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.queries.GetAllUsersQuery;
import aurora.supply_wok.platform.iam.domain.model.queries.GetUserByIdQuery;
import aurora.supply_wok.platform.iam.domain.model.queries.GetUserByEmailQuery;

import java.util.List;
import java.util.Optional;

/**
 * Application service contract for IAM user read queries.
 */
public interface UserQueryService {
    /**
     * Handles retrieval of all users.
     *
     * @param query query marker
     * @return list of users
     */
    List<User> handle(GetAllUsersQuery query);

    /**
     * Handles retrieval of a user by id.
     *
     * @param query user-id query
     * @return matching user, if found
     */
    Optional<User> handle(GetUserByIdQuery query);

    /**
     * Handles retrieval of a user by email.
     *
     * @param query email query
     * @return matching user, if found
     */
    Optional<User> handle(GetUserByEmailQuery query);

}

