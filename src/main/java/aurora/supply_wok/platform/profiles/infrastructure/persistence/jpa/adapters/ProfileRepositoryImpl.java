package aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.adapters;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.domain.repositories.ProfileRepository;
import aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.assemblers.ProfilePersistenceAssembler;
import aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.repositories.ProfilePersistenceRepository;
import aurora.supply_wok.platform.shared.infrastructure.events.DomainEventPublisher;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.List;

/**
 * Repository adapter that bridges the profile repository port with Spring Data JPA.
 */
@Repository
public class ProfileRepositoryImpl implements ProfileRepository {

    private final ProfilePersistenceRepository profilePersistenceRepository;
    private final DomainEventPublisher domainEventPublisher;

    public ProfileRepositoryImpl(ProfilePersistenceRepository profilePersistenceRepository,
                                 DomainEventPublisher domainEventPublisher) {
        this.profilePersistenceRepository = profilePersistenceRepository;
        this.domainEventPublisher = domainEventPublisher;
    }

    @Override
    public Optional<Profile> findByProfileType(EProfileType profileType) {
        return profilePersistenceRepository.findAllByProfileType(profileType).stream()
                .findFirst()
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public Optional<Profile> findByProfileTypeAndEmail(EProfileType profileType, String email) {
        return profilePersistenceRepository.findByProfileTypeAndEmailIgnoreCase(profileType, normalizeEmail(email))
                .map(ProfilePersistenceAssembler::toDomainFromPersistence);
    }

    @Override
    public List<Profile> findAllByProfileType(EProfileType profileType) {
        return profilePersistenceRepository.findAllByProfileType(profileType).stream()
                .map(ProfilePersistenceAssembler::toDomainFromPersistence)
                .toList();
    }

    @Override
    public Profile save(Profile profile) {
        var isNew = profile.getId() == null;
        var saved = profilePersistenceRepository.save(ProfilePersistenceAssembler.toPersistenceFromDomain(profile));
        if (!isNew) {
            domainEventPublisher.publishAndClear(profile);
        }

        var savedProfile = ProfilePersistenceAssembler.toDomainFromPersistence(saved);
        if (isNew) {
            savedProfile.onCreated();
            domainEventPublisher.publishAndClear(savedProfile);
        }
        return savedProfile;
    }

    private static String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
