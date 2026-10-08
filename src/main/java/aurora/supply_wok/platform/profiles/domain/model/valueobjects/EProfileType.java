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

        String normalized = value.trim().toUpperCase();
        if (normalized.endsWith("S")) {
            String singular = normalized.substring(0, normalized.length() - 1);
            try {
                return EProfileType.valueOf(singular);
            } catch (IllegalArgumentException ignored) {
            }
        }

        return EProfileType.valueOf(normalized);
    }
}
