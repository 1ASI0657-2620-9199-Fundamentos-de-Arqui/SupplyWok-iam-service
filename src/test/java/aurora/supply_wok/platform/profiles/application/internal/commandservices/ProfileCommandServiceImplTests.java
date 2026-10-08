package aurora.supply_wok.platform.profiles.application.internal.commandservices;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.domain.repositories.ProfileRepository;
import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileCommandServiceImplTests {

    @Mock
    private ProfileRepository profileRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private ProfileCommandServiceImpl profileCommandService;

    @Test
    void handle_whenProfileExistsByEmail_updatesAndSavesExistingProfile() {
        // Arrange
        var command = new UpdateProfileCommand(
                EProfileType.RESTAURANT, "Wok House", "Carlos", "Perez", "carlos@wok.pe",
                "Street 1", "District 1", "Lima", "Peru", "+51999999", true, false
        );
        var existing = new Profile(EProfileType.RESTAURANT);
        existing.setId(10L);
        when(profileRepository.findByProfileTypeAndEmail(EProfileType.RESTAURANT, "carlos@wok.pe"))
                .thenReturn(Optional.of(existing));
        when(profileRepository.save(existing)).thenReturn(existing);

        // Act
        var result = profileCommandService.handle(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBusinessName()).isEqualTo("Wok House");
        verify(profileRepository).save(existing);
        verify(eventPublisher, never()).publishEvent(any());
    }

    @Test
    void handle_whenProfileNotFoundByEmailAndOtherProfileOfTypeExists_createsNewProfileWithoutReusingOther() {
        // Arrange
        var command = new UpdateProfileCommand(
                EProfileType.RESTAURANT, "Wok House", "Carlos", "Perez", "newemail@wok.pe",
                "Street 1", "District 1", "Lima", "Peru", "+51999999", true, false
        );
        when(profileRepository.findByProfileTypeAndEmail(EProfileType.RESTAURANT, "newemail@wok.pe"))
                .thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = profileCommandService.handle(command);

        // Assert
        assertThat(result.getId()).isNull();
        assertThat(result.getEmail()).isEqualTo("newemail@wok.pe");
        verify(profileRepository, never()).findAllByProfileType(any());
    }

    @Test
    void handle_whenProfileNotFoundAndMultipleProfilesExist_createsNewProfile() {
        // Arrange
        var command = new UpdateProfileCommand(
                EProfileType.RESTAURANT, "Wok House", "Carlos", "Perez", "newemail@wok.pe",
                "Street 1", "District 1", "Lima", "Peru", "+51999999", true, false
        );
        when(profileRepository.findByProfileTypeAndEmail(EProfileType.RESTAURANT, "newemail@wok.pe"))
                .thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = profileCommandService.handle(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBusinessName()).isEqualTo("Wok House");
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void handle_whenProfileNotFoundAndNoProfilesExist_createsNewProfile() {
        // Arrange
        var command = new UpdateProfileCommand(
                EProfileType.RESTAURANT, "Wok House", "Carlos", "Perez", "newemail@wok.pe",
                "Street 1", "District 1", "Lima", "Peru", "+51999999", true, false
        );
        when(profileRepository.findByProfileTypeAndEmail(EProfileType.RESTAURANT, "newemail@wok.pe"))
                .thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = profileCommandService.handle(command);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getBusinessName()).isEqualTo("Wok House");
        verify(profileRepository).save(any(Profile.class));
    }

    @Test
    void handle_whenProfileTypeIsSupplier_publishesSupplierProfileSyncRequestedIntegrationEvent() {
        // Arrange
        var command = new UpdateProfileCommand(
                EProfileType.SUPPLIER, "Insumos Andinos", "Maria", "Quispe", "maria@insumos.pe",
                "Street 2", "District 2", "Lima", "Peru", "+51988888", true, false
        );
        when(profileRepository.findByProfileTypeAndEmail(EProfileType.SUPPLIER, "maria@insumos.pe"))
                .thenReturn(Optional.empty());
        when(profileRepository.save(any(Profile.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var result = profileCommandService.handle(command);

        // Assert
        assertThat(result).isNotNull();
        verify(eventPublisher).publishEvent(any(SupplierProfileSyncRequestedIntegrationEvent.class));
    }
}
