package aurora.supply_wok.platform.iam.infrastructure.persistence.jpa.adapters;

import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import aurora.supply_wok.platform.iam.infrastructure.persistence.jpa.assemblers.UserPersistenceAssembler;
import aurora.supply_wok.platform.iam.infrastructure.persistence.jpa.repositories.UserPersistenceRepository;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository adapter that bridges the IAM user domain repository port with Spring Data JPA.
 */
@Repository
public class UserRepositoryImpl implements UserRepository {

    private final UserPersistenceRepository userPersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public UserRepositoryImpl(UserPersistenceRepository userPersistenceRepository,
                              DomainEventPublisher domainEventPublisher) {
        this.userPersistenceRepository = userPersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userPersistenceRepository.findById(id).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userPersistenceRepository.findByEmail(email).map(UserPersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<User> findAll() {
        return userPersistenceRepository.findAll().stream().map(UserPersistenceAssembler::toDomainFromPersistence).toList();
    }

    @Override
    public User save(User user) {
        var isNew = user.getId() == null;
        var saved = userPersistenceRepository.save(UserPersistenceAssembler.toPersistenceFromDomain(user));
        var savedUser = UserPersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedUser.onSignedUp();
            domainEventPublisher.publishAndClear(savedUser);
        } else {
            domainEventPublisher.publishAndClear(user);
        }
        return savedUser;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userPersistenceRepository.existsByEmail(email);
    }
}

