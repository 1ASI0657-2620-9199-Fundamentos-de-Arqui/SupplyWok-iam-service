package aurora.supply_wok.platform.iam.interfaces.rest;

import aurora.supply_wok.platform.iam.application.commandservices.UserCommandService;
import aurora.supply_wok.platform.iam.application.internal.outboundservices.tokens.TokenService;
import aurora.supply_wok.platform.iam.domain.model.aggregates.User;
import aurora.supply_wok.platform.iam.domain.model.commands.SignUpCommand;
import aurora.supply_wok.platform.iam.domain.model.valueobjects.Roles;
import aurora.supply_wok.platform.iam.domain.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UsersControllerSecurityTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCommandService userCommandService;

    private String adminToken;
    private String restaurantToken;
    private String supplierToken;
    private Long restaurantUserId;

    @BeforeEach
    void setUp() {
        var restaurantUser = userRepository.findByEmail("restaurante@ejemplo.com")
                .orElseGet(() -> userCommandService.handle(new SignUpCommand("restaurante@ejemplo.com", "Password123!", "RESTAURANT")).value());
        restaurantUserId = restaurantUser.getId();

        var adminUser = userRepository.findByEmail("admin@supplywok.pe")
                .orElseGet(() -> {
                    var u = new User("admin@supplywok.pe", "Password123!", Roles.ADMIN);
                    userRepository.save(u);
                    return userRepository.findByEmail("admin@supplywok.pe").orElseThrow();
                });

        adminToken = tokenService.generateToken("admin@supplywok.pe", adminUser.getId(), List.of("ADMIN"));
        restaurantToken = tokenService.generateToken("restaurante@ejemplo.com", restaurantUserId, List.of("RESTAURANT"));
        supplierToken = tokenService.generateToken("proveedor@ejemplo.com", 2L, List.of("SUPPLIER"));
    }

    @Test
    void getAllUsers_callerHasAdminRole_returnsOk() throws Exception {
        // Arrange
        var request = get("/api/v1/users")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk());
    }

    @Test
    void getAllUsers_callerHasRestaurantRole_returnsForbidden() throws Exception {
        // Arrange
        var request = get("/api/v1/users")
                .header("Authorization", "Bearer " + restaurantToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_callerHasSupplierRole_returnsForbidden() throws Exception {
        // Arrange
        var request = get("/api/v1/users")
                .header("Authorization", "Bearer " + supplierToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void getAllUsers_unauthenticated_returnsUnauthorized() throws Exception {
        // Arrange
        var request = get("/api/v1/users")
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getUserById_callerIsOwner_returnsOk() throws Exception {
        // Arrange
        var request = get("/api/v1/users/" + restaurantUserId)
                .header("Authorization", "Bearer " + restaurantToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantUserId))
                .andExpect(jsonPath("$.email").value("restaurante@ejemplo.com"));
    }

    @Test
    void getUserById_callerIsAdmin_returnsOk() throws Exception {
        // Arrange
        var request = get("/api/v1/users/" + restaurantUserId)
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(restaurantUserId));
    }

    @Test
    void getUserById_callerIsNotOwnerAndNotAdmin_returnsForbidden() throws Exception {
        // Arrange
        var request = get("/api/v1/users/" + restaurantUserId)
                .header("Authorization", "Bearer " + supplierToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isForbidden());
    }

    @Test
    void getUserById_callerIsAdminAndUserNotFound_returnsNotFound() throws Exception {
        // Arrange: admin requests user id 99999 who does not exist in the database
        var request = get("/api/v1/users/99999")
                .header("Authorization", "Bearer " + adminToken)
                .accept(MediaType.APPLICATION_JSON);

        // Act & Assert
        mockMvc.perform(request)
                .andExpect(status().isNotFound());
    }
}
