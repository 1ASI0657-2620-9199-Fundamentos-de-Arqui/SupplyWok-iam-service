package aurora.supply_wok.platform.profiles.application.internal.eventhandlers;

import aurora.supply_wok.platform.profiles.application.commandservices.ProfileCommandService;
import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.domain.repositories.ProfileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ProfilesDatabaseSeeder {
    private final ProfileRepository profileRepository;
    private final ProfileCommandService profileCommandService;

    public ProfilesDatabaseSeeder(ProfileRepository profileRepository, ProfileCommandService profileCommandService) {
        this.profileRepository = profileRepository;
        this.profileCommandService = profileCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void on(ApplicationReadyEvent event) {
        if (profileRepository.findAllByProfileType(EProfileType.RESTAURANT).isEmpty() &&
            profileRepository.findAllByProfileType(EProfileType.SUPPLIER).isEmpty()) {
            log.info("Seeding profiles...");

            profileCommandService.handle(new UpdateProfileCommand(
                    EProfileType.RESTAURANT,
                    "Wok Restaurante Central",
                    "Juan",
                    "Perez",
                    "restaurante@ejemplo.com",
                    "Calle Principal 123",
                    "Centro",
                    "Lima",
                    "Peru",
                    "+51999999999",
                    true,
                    false
            ));

            profileCommandService.handle(new UpdateProfileCommand(
                    EProfileType.SUPPLIER,
                    "Verduras Frescas S.A.",
                    "Carlos",
                    "Gomez",
                    "proveedor@ejemplo.com",
                    "Avenida Agricola 456",
                    "Industrial",
                    "Lima",
                    "Peru",
                    "+51888888888",
                    true,
                    false
            ));

            log.info("Profiles seeded successfully.");
        }
    }
}
