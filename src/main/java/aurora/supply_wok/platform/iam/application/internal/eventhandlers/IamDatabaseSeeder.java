package aurora.supply_wok.platform.iam.application.internal.eventhandlers;

import aurora.supply_wok.platform.iam.application.commandservices.UserCommandService;
import aurora.supply_wok.platform.iam.domain.model.commands.SignUpCommand;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class IamDatabaseSeeder {
    private final UserRepository userRepository;
    private final UserCommandService userCommandService;

    public IamDatabaseSeeder(UserRepository userRepository, UserCommandService userCommandService) {
        this.userRepository = userRepository;
        this.userCommandService = userCommandService;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void on(ApplicationReadyEvent event) {
        if (userRepository.findAll().isEmpty()) {
            log.info("Seeding IAM users...");
            userCommandService.handle(new SignUpCommand("restaurante@ejemplo.com", "Password123!", "RESTAURANT"));
            userCommandService.handle(new SignUpCommand("proveedor@ejemplo.com", "Password123!", "SUPPLIER"));
            log.info("IAM users seeded successfully.");
        }
    }
}
