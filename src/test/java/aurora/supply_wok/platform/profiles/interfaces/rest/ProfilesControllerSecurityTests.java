package aurora.supply_wok.platform.profiles.interfaces.rest;

import aurora.supply_wok.platform.iam.application.commandservices.UserCommandService;
import aurora.supply_wok.platform.iam.application.internal.outboundservices.tokens.TokenService;
import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.commands.SignUpCommand;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import aurora.supply_wok.platform.profiles.application.commandservices.ProfileCommandService;
import aurora.supply_wok.platform.profiles.domain.model.commands.UpdateProfileCommand;
import aurora.supply_wok.platform.profiles.domain.model.valueobjects.EProfileType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class ProfilesControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCommandService userCommandService;

    @Autowired
    private ProfileCommandService profileCommandService;

    private String adminToken;
    private String restaurantToken;
    private String secondRestaurantToken;
    private String supplierToken;

    @BeforeEach
    void setUp() {
        var restaurantUser = userRepository.findByEmail("restaurante@ejemplo.com")
                .orElseGet(() -> userCommandService.handle(new SignUpCommand("restaurante@ejemplo.com", "Password123!", "RESTAURANT")).value());

        var secondRestaurantUser = userRepository.findByEmail("restaurante2@ejemplo.com")
                .orElseGet(() -> userCommandService.handle(new SignUpCommand("restaurante2@ejemplo.com", "Password123!", "RESTAURANT")).value());

        var supplierUser = userRepository.findByEmail("proveedor@ejemplo.com")
                .orElseGet(() -> userCommandService.handle(new SignUpCommand("proveedor@ejemplo.com", "Password123!", "SUPPLIER")).value());

        var adminUser = userRepository.findByEmail("admin@supplywok.pe")
                .orElseGet(() -> {
                    var u = new User("admin@supplywok.pe", "Password123!", Roles.ADMIN);
                    userRepository.save(u);
                    return userRepository.findByEmail("admin@supplywok.pe").orElseThrow();
                });

        // Ensure distinct profile records exist for both restaurants
        profileCommandService.handle(new UpdateProfileCommand(
                EProfileType.RESTAURANT,
                "Restaurante Uno",
                "Uno",
                "Dueño",
                "restaurante@ejemplo.com",
                "Calle 1",
                "Distrito 1",
                "Lima",
                "Peru",
                "+51999111222",
                true,
                false
        ));

        profileCommandService.handle(new UpdateProfileCommand(
                EProfileType.RESTAURANT,
                "Restaurante Dos",
                "Dos",
                "Dueño",
                "restaurante2@ejemplo.com",
                "Calle 2",
                "Distrito 2",
                "Lima",
                "Peru",
                "+51999333444",
                true,
                false
        ));

        adminToken = tokenService.generateToken("admin@supplywok.pe", adminUser.getId(), List.of("ADMIN"));
        restaurantToken = tokenService.generateToken("restaurante@ejemplo.com", restaurantUser.getId(), List.of("RESTAURANT"));
        secondRestaurantToken = tokenService.generateToken("restaurante2@ejemplo.com", secondRestaurantUser.getId(), List.of("RESTAURANT"));
        supplierToken = tokenService.generateToken("proveedor@ejemplo.com", supplierUser.getId(), List.of("SUPPLIER"));
    }

    @Test
    void getProfileByType_authenticatedCaller_returnsCallerProfile() throws Exception {
        // Arrange
        var request = get("/api/v1/profiles/restaurants")
                .header("Authorization", "Bearer " + restaurantToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("restaurante@ejemplo.com"))
                .andExpect(jsonPath("$.businessName").value("Restaurante Uno"));
    }

    @Test
    void getProfileByType_differentCaller_returnsTheirOwnProfileNeverFirstProfile() throws Exception {
        // Arrange: second restaurant calls GET /api/v1/profiles/restaurants
        var request = get("/api/v1/profiles/restaurants")
                .header("Authorization", "Bearer " + secondRestaurantToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("restaurante2@ejemplo.com"))
                .andExpect(jsonPath("$.businessName").value("Restaurante Dos"));
    }

    @Test
    void updateProfile_bodyHasDifferentEmail_operatesOnCallerEmailFromToken() throws Exception {
        // Arrange: caller has token for restaurante@ejemplo.com, but payload body specifies attacker@other.com
        String jsonPayload = """
                {
                    "businessName": "Updated Name",
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "email": "attacker@other.com",
                    "street": "Nueva Calle 789",
                    "district": "Miraflores",
                    "city": "Lima",
                    "country": "Peru",
                    "supportContact": "+51999999999",
                    "emailNotifications": true,
                    "smsNotifications": false
                }
                """;

        var request = put("/api/v1/profiles/restaurants")
                .header("Authorization", "Bearer " + restaurantToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert: result must be associated with the caller's email from the token, ignoring the body email
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("restaurante@ejemplo.com"))
                .andExpect(jsonPath("$.businessName").value("Updated Name"));
    }

    @Test
    void getProfilesByType_anyAuthenticatedCaller_returnsOk() throws Exception {
        // Arrange: restaurants browsing suppliers directory
        var request = get("/api/v1/profiles/suppliers/accounts")
                .header("Authorization", "Bearer " + restaurantToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void getProfileByTypeAndEmail_callerEmailMatches_returnsOk() throws Exception {
        // Arrange
        var request = get("/api/v1/profiles/restaurants/accounts/by-email")
                .param("email", "restaurante@ejemplo.com")
                .header("Authorization", "Bearer " + restaurantToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("restaurante@ejemplo.com"));
    }

    @Test
    void getProfileByTypeAndEmail_callerIsAdmin_returnsOk() throws Exception {
        // Arrange
        var request = get("/api/v1/profiles/restaurants/accounts/by-email")
                .param("email", "restaurante@ejemplo.com")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("restaurante@ejemplo.com"));
    }

    @Test
    void getProfileByTypeAndEmail_callerDoesNotMatchAndNotAdmin_returnsForbidden() throws Exception {
        // Arrange: supplier tries to inspect restaurant's profile by email
        var request = get("/api/v1/profiles/restaurants/accounts/by-email")
                .param("email", "restaurante@ejemplo.com")
                .header("Authorization", "Bearer " + supplierToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void updateProfileByEmail_callerEmailMatches_returnsOk() throws Exception {
        // Arrange
        String jsonPayload = """
                {
                    "businessName": "Restaurante Actualizado",
                    "firstName": "Juan",
                    "lastName": "Perez",
                    "email": "restaurante@ejemplo.com",
                    "street": "Calle 100",
                    "district": "Surco",
                    "city": "Lima",
                    "country": "Peru",
                    "supportContact": "+51999999999",
                    "emailNotifications": true,
                    "smsNotifications": false
                }
                """;

        var request = put("/api/v1/profiles/restaurants/accounts/by-email")
                .param("email", "restaurante@ejemplo.com")
                .header("Authorization", "Bearer " + restaurantToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("restaurante@ejemplo.com"));
    }

    @Test
    void updateProfileByEmail_callerIsAdmin_returnsOk() throws Exception {
        // Arrange
        String jsonPayload = """
                {
                    "businessName": "Admin Edited",
                    "firstName": "Admin",
                    "lastName": "User",
                    "email": "restaurante@ejemplo.com",
                    "street": "Calle Admin",
                    "district": "San Isidro",
                    "city": "Lima",
                    "country": "Peru",
                    "supportContact": "+51999999999",
                    "emailNotifications": true,
                    "smsNotifications": false
                }
                """;

        var request = put("/api/v1/profiles/restaurants/accounts/by-email")
                .param("email", "restaurante@ejemplo.com")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("restaurante@ejemplo.com"));
    }

    @Test
    void updateProfileByEmail_callerDoesNotMatchAndNotAdmin_returnsForbidden() throws Exception {
        // Arrange
        String jsonPayload = """
                {
                    "businessName": "Unauthorized Edit",
                    "firstName": "Attacker",
                    "lastName": "User",
                    "email": "restaurante@ejemplo.com",
                    "street": "Calle Maliciosa",
                    "district": "Centro",
                    "city": "Lima",
                    "country": "Peru",
                    "supportContact": "+51999999999",
                    "emailNotifications": true,
                    "smsNotifications": false
                }
                """;

        var request = put("/api/v1/profiles/restaurants/accounts/by-email")
                .param("email", "restaurante@ejemplo.com")
                .header("Authorization", "Bearer " + supplierToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(jsonPayload)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void getProfileByType_unauthenticated_returnsUnauthorized() throws Exception {
        // Arrange
        var request = get("/api/v1/profiles/restaurants")
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }
}
