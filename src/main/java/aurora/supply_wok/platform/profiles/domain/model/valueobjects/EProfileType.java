package aurora.supply_wok.platform.profiles.domain.model.valueobjects;

/**
 * Supported operational profile scopes.
 */
public enum EProfileType {
    RESTAURANT,
    SUPPLIER;

    public static EProfileType fromPath(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Profile type is required.");
        }

        return EProfileType.valueOf(value.trim().toUpperCase());
    }
}
