package aurora.supply_wok.platform.profiles.acceptance;

import aurora.supply_wok.platform.profiles.application.internal.commandservices.ProfileCommandServiceImpl;
import aurora.supply_wok.platform.profiles.domain.model.aggregates.Profile;
import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.profiles.domain.repositories.ProfileRepository;
import aurora.supply_wok.platform.profiles.interfaces.events.SupplierProfileSyncRequestedIntegrationEvent;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.mockito.Mockito;
import org.springframework.context.ApplicationEventPublisher;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ProfileManagementSteps {

    private ProfileRepository profileRepository;
    private ApplicationEventPublisher eventPublisher;
    private ProfileCommandServiceImpl profileCommandService;

    private Map<String, Profile> profileMap;

    @Before
    public void setUp() {
        profileRepository = Mockito.mock(ProfileRepository.class);
        eventPublisher = Mockito.mock(ApplicationEventPublisher.class);
        profileCommandService = new ProfileCommandServiceImpl(profileRepository, eventPublisher);
        profileMap = new HashMap<>();

        when(profileRepository.findByProfileTypeAndEmail(any(EProfileType.class), any(String.class)))
                .thenAnswer(inv -> {
                    EProfileType type = inv.getArgument(0);
                    String email = inv.getArgument(1);
                    Profile profile = profileMap.get(email);
                    if (profile != null && profile.getProfileType() == type) {
                        return Optional.of(profile);
                    }
                    return Optional.empty();
                });

        when(profileRepository.save(any(Profile.class)))
                .thenAnswer(inv -> {
                    Profile p = inv.getArgument(0);
                    profileMap.put(p.getEmail(), p);
                    return p;
                });
    }

    @Given("an existing profile for {string} with business name {string}")
    public void anExistingProfileForWithBusinessName(String email, String businessName) {
        var profile = new Profile(
                EProfileType.RESTAURANT, businessName, "Carlos", "Perez", email,
                "Av. Principal 123", "Miraflores", "Lima", "Peru", "+51999111222",
                true, false
        );
        profile.setId((long) (profileMap.size() + 1));
        profileMap.put(email, profile);
    }

    @Given("an existing profile for {string} of type {string}")
    public void anExistingProfileForOfType(String email, String typeStr) {
        EProfileType type = EProfileType.valueOf(typeStr);
        var profile = new Profile(
                type, "Distribuidora Fresh Andes", "Maria", "Quispe", email,
                "Calle Los Sauces 456", "San Isidro", "Lima", "Peru", "+51999333444",
                true, false
        );
        profile.setId((long) (profileMap.size() + 1));
        profileMap.put(email, profile);
    }

    @When("{string} updates their business name to {string}")
    public void updatesTheirBusinessName(String email, String newBusinessName) {
        Profile current = profileMap.get(email);
        EProfileType type = current != null ? current.getProfileType() : EProfileType.RESTAURANT;
        String firstName = current != null ? current.getFirstName() : "Default";
        String lastName = current != null ? current.getLastName() : "User";
        String street = current != null ? current.getStreet() : "Street";
        String district = current != null ? current.getDistrict() : "District";
        String city = current != null ? current.getCity() : "City";
        String country = current != null ? current.getCountry() : "Country";
        String supportContact = current != null ? current.getSupportContact() : "+51999000111";

        var command = new UpdateProfileCommand(
                type, newBusinessName, firstName, lastName, email,
                street, district, city, country, supportContact, true, false
        );
        profileCommandService.handle(command);
    }

    @When("{string} updates their contact details to city {string} and phone {string}")
    public void updatesTheirContactDetails(String email, String city, String phone) {
        Profile current = profileMap.get(email);
        EProfileType type = current != null ? current.getProfileType() : EProfileType.RESTAURANT;
        String businessName = current != null ? current.getBusinessName() : "Business";
        String firstName = current != null ? current.getFirstName() : "Carlos";
        String lastName = current != null ? current.getLastName() : "Perez";
        String street = current != null ? current.getStreet() : "Street";
        String district = current != null ? current.getDistrict() : "District";
        String country = current != null ? current.getCountry() : "Peru";

        var command = new UpdateProfileCommand(
                type, businessName, firstName, lastName, email,
                street, district, city, country, phone, true, false
        );
        profileCommandService.handle(command);
    }

    @Then("the profile of {string} has business name {string}")
    public void theProfileOfHasBusinessName(String email, String expectedName) {
        Profile profile = profileMap.get(email);
        assertThat(profile).isNotNull();
        assertThat(profile.getBusinessName()).isEqualTo(expectedName);
    }

    @And("the profile of {string} still has business name {string}")
    public void theProfileOfStillHasBusinessName(String email, String expectedName) {
        Profile profile = profileMap.get(email);
        assertThat(profile).isNotNull();
        assertThat(profile.getBusinessName()).isEqualTo(expectedName);
    }

    @Then("the profile of {string} reflects city {string} and support contact {string}")
    public void theProfileOfReflectsCityAndSupportContact(String email, String expectedCity, String expectedContact) {
        Profile profile = profileMap.get(email);
        assertThat(profile).isNotNull();
        assertThat(profile.getCity()).isEqualTo(expectedCity);
        assertThat(profile.getSupportContact()).isEqualTo(expectedContact);
    }

    @And("a supplier profile sync event is published")
    public void aSupplierProfileSyncEventIsPublished() {
        verify(eventPublisher).publishEvent(any(SupplierProfileSyncRequestedIntegrationEvent.class));
    }
}
