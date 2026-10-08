package aurora.supply_wok.platform.iam.infrastructure.persistence.jpa.entities;

import aurora.supply_wok.platform.shared.infrastructure.persistence.jpa.entities.AuditableAbstractPersistenceEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

/**
 * JPA persistence entity for IAM users.
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class UserPersistenceEntity extends AuditableAbstractPersistenceEntity {

    // Keep the legacy column name to avoid forcing a DB migration while moving IAM to email-based auth.
    @Column(name = "username", nullable = false, unique = true, length = 120)
    private String email;

    @Column(name = "password", nullable = false, length = 120)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles role;
}

