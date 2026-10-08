package aurora.supply_wok.platform.shared.domain.exceptions;

/**
 * Exception thrown when a requested resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceName, Object identifier) {
        super("%s with identifier %s was not found".formatted(resourceName, identifier));
    }
}
