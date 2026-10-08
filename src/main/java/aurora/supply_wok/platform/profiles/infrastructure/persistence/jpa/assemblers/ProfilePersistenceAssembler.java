package aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.assemblers;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.infrastructure.persistence.jpa.entities.ProfilePersistenceEntity;

/**
 * Static mapper between profile domain and persistence representations.
 */
public final class ProfilePersistenceAssembler {

    private ProfilePersistenceAssembler() {
    }

    public static Profile toDomainFromPersistence(ProfilePersistenceEntity entity) {
        var profile = new Profile(
                entity.getProfileType(),
                entity.getBusinessName(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getStreet(),
                entity.getDistrict(),
                entity.getCity(),
                entity.getCountry(),
                entity.getSupportContact(),
                entity.isEmailNotifications(),
                entity.isSmsNotifications()
        );
        profile.setId(entity.getId());
        return profile;
    }

    public static ProfilePersistenceEntity toPersistenceFromDomain(Profile profile) {
        var entity = new ProfilePersistenceEntity();
        entity.setId(profile.getId());
        entity.setProfileType(profile.getProfileType());
        entity.setBusinessName(profile.getBusinessName());
        entity.setFirstName(profile.getFirstName());
        entity.setLastName(profile.getLastName());
        entity.setEmail(profile.getEmail());
        entity.setStreet(profile.getStreet());
        entity.setDistrict(profile.getDistrict());
        entity.setCity(profile.getCity());
        entity.setCountry(profile.getCountry());
        entity.setSupportContact(profile.getSupportContact());
        entity.setEmailNotifications(profile.isEmailNotifications());
        entity.setSmsNotifications(profile.isSmsNotifications());
        return entity;
    }
}
