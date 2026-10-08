package aurora.supply_wok.platform.profiles.interfaces.events;

public record SupplierProfileSyncRequestedIntegrationEvent(
        String name,
        String contactName,
        String email,
        String phone
) {
}
