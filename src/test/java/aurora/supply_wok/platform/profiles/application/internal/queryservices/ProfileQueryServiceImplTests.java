package aurora.supply_wok.platform.profiles.application.internal.queryservices;

import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeAndEmailQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfileByTypeQuery;
import aurora.supply_wok.platform.profiles.domain.model.queries.GetProfilesByTypeQuery;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.domain.repositories.ProfileRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProfileQueryServiceImplTests {

    @Mock
    private ProfileRepository profileRepository;

    @InjectMocks
    private ProfileQueryServiceImpl profileQueryService;

    @Test
    void handle_whenGetProfileByTypeQuery_delegatesToRepository() {
        // Arrange
        var profile = new Profile(EProfileType.RESTAURANT);
        when(profileRepository.findByProfileType(EProfileType.RESTAURANT)).thenReturn(Optional.of(profile));
        var query = new GetProfileByTypeQuery(EProfileType.RESTAURANT);

        // Act
        var result = profileQueryService.handle(query);

        // Assert
        assertThat(result).contains(profile);
        verify(profileRepository).findByProfileType(EProfileType.RESTAURANT);
    }

    @Test
    void handle_whenGetProfileByTypeAndEmailQuery_delegatesToRepository() {
        // Arrange
        var profile = new Profile(EProfileType.SUPPLIER);
        when(profileRepository.findByProfileTypeAndEmail(EProfileType.SUPPLIER, "sup@wok.pe"))
                .thenReturn(Optional.of(profile));
        var query = new GetProfileByTypeAndEmailQuery(EProfileType.SUPPLIER, "sup@wok.pe");

        // Act
        var result = profileQueryService.handle(query);

        // Assert
        assertThat(result).contains(profile);
        verify(profileRepository).findByProfileTypeAndEmail(EProfileType.SUPPLIER, "sup@wok.pe");
    }

    @Test
    void handle_whenGetProfilesByTypeQuery_delegatesToRepository() {
        // Arrange
        var profiles = List.of(new Profile(EProfileType.RESTAURANT));
        when(profileRepository.findAllByProfileType(EProfileType.RESTAURANT)).thenReturn(profiles);
        var query = new GetProfilesByTypeQuery(EProfileType.RESTAURANT);

        // Act
        var result = profileQueryService.handle(query);

        // Assert
        assertThat(result).isEqualTo(profiles);
        verify(profileRepository).findAllByProfileType(EProfileType.RESTAURANT);
    }
}
