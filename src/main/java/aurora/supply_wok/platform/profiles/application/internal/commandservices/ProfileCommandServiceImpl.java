package aurora.supply_wok.platform.profiles.application.internal.commandservices;

import aurora.supply_wok.platform.profiles.application.commandservices.ProfileCommandService;
import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.domain.repositories.ProfileRepository;
import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Default profile command service implementation.
 */
@Service
public class ProfileCommandServiceImpl implements ProfileCommandService {

    private final ProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileCommandServiceImpl(ProfileRepository profileRepository,
                                     ApplicationEventPublisher eventPublisher) {
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Profile handle(UpdateProfileCommand command) {
        var existingProfile = profileRepository.findByProfileTypeAndEmail(command.profileType(), command.email())
                .or(() -> {
                    var profiles = profileRepository.findAllByProfileType(command.profileType());
                    return profiles.size() == 1 ? Optional.of(profiles.get(0)) : Optional.empty();
                });
        var profile = existingProfile.orElseGet(() -> new Profile(command.profileType()));
        profile.update(command);
        if (existingProfile.isPresent()) {
            profile.onUpdated();
        }
        var savedProfile = profileRepository.save(profile);
        if (command.profileType() == EProfileType.SUPPLIER) {
            eventPublisher.publishEvent(new SupplierProfileSyncRequestedIntegrationEvent(
                    command.businessName(),
                    "%s %s".formatted(command.firstName(), command.lastName()).trim(),
                    command.email(),
                    command.supportContact()
            ));
        }
        return savedProfile;
    }
}
