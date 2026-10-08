package aurora.supply_wok.platform.iam.interfaces.rest.transform;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.interfaces.rest.resources.UserResource;
public class UserResourceFromEntityAssembler {
    /**
     * Converts a user aggregate to its REST representation.
     *
     * @param user user aggregate
     * @return user resource
     */
    public static UserResource toResourceFromEntity(User user) {
        return new UserResource(user.getId(), user.getEmail(), user.getRole().name());
    }
}
