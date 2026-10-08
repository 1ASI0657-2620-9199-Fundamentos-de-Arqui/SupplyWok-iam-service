package aurora.supply_wok.platform.profiles.domain.model.aggregates;

import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.events.ProfileCreatedEvent;
import aurora.supply_wok.platform.profiles.domain.model.events.ProfileUpdatedEvent;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import aurora.supply_wok.platform.shared.domain.model.aggregates.AbstractDomainAggregateRoot;
import lombok.Getter;
import lombok.Setter;

/**
 * Profile aggregate root for restaurant and supplier account settings.
 */
@Getter
public class Profile extends AbstractDomainAggregateRoot<Profile> {

    @Setter
    private Long id;

    private EProfileType profileType;
    private String businessName;
    private String firstName;
    private String lastName;
    private String email;
    private String street;
    private String district;
    private String city;
    private String country;
    private String supportContact;
    private boolean emailNotifications;
    private boolean smsNotifications;

    public Profile() {
        this(EProfileType.RESTAURANT);
    }

    public Profile(EProfileType profileType) {
        this.profileType = profileType;
        this.businessName = defaultBusinessName(profileType);
        this.firstName = "";
        this.lastName = "";
        this.email = "";
        this.street = "";
        this.district = "";
        this.city = "";
        this.country = "";
        this.supportContact = "";
        this.emailNotifications = true;
        this.smsNotifications = false;
    }

    public Profile(EProfileType profileType, String businessName, String firstName, String lastName, String email,
                   String street, String district, String city, String country, String supportContact,
                   boolean emailNotifications, boolean smsNotifications) {
        this(profileType);
        update(businessName, firstName, lastName, email, street, district, city, country, supportContact,
                emailNotifications, smsNotifications);
    }

    public static Profile defaultFor(EProfileType profileType) {
        return new Profile(profileType);
    }

    public void update(UpdateProfileCommand command) {
        update(command.businessName(), command.firstName(), command.lastName(), command.email(), command.street(),
                command.district(), command.city(), command.country(), command.supportContact(),
                command.emailNotifications(), command.smsNotifications());
    }

    public void onCreated() {
        registerDomainEvent(ProfileCreatedEvent.from(this));
    }

    public void onUpdated() {
        registerDomainEvent(ProfileUpdatedEvent.from(this));
    }

    private void update(String businessName, String firstName, String lastName, String email, String street,
                        String district, String city, String country, String supportContact,
                        boolean emailNotifications, boolean smsNotifications) {
        this.businessName = normalize(businessName);
        this.firstName = normalize(firstName);
        this.lastName = normalize(lastName);
        this.email = normalize(email);
        this.street = normalize(street);
        this.district = normalize(district);
        this.city = normalize(city);
        this.country = normalize(country);
        this.supportContact = normalize(supportContact);
        this.emailNotifications = emailNotifications;
        this.smsNotifications = smsNotifications;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private static String defaultBusinessName(EProfileType profileType) {
        return profileType == EProfileType.SUPPLIER ? "Distribuidora Fresh Andes" : "La Cucina Bella";
    }
}
