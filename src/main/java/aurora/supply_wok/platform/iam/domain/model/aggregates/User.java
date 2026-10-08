package aurora.supply_wok.platform.iam.domain.model.aggregates;

import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.domain.model.events.UserSignedUpEvent;
import aurora.supply_wok.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * User aggregate root.
 */
@Getter
public class User extends AbstractDomainAggregateRoot<User> {

    @Setter
    private Long id;
    @Setter
    private String email;
    @Setter
    private String password;
    @Setter
    private Roles role;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public User(String email, String password, Roles role) {
        this(email, password);
        this.role = role;
    }

    public void onSignedUp() {
        registerDomainEvent(UserSignedUpEvent.from(this));
    }
}
