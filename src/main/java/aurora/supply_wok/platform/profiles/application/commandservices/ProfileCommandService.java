package aurora.supply_wok.platform.profiles.application.commandservices;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;

/**
 * Application contract for profile write use cases.
 */
public interface ProfileCommandService {
    Profile handle(UpdateProfileCommand command);
}
