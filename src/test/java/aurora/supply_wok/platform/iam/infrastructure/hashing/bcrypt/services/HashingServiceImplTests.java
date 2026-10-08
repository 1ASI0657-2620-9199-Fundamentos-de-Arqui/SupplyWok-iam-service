package aurora.supply_wok.platform.iam.infrastructure.hashing.bcrypt.services;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HashingServiceImplTests {

    @Test
    void encode_whenRawPasswordProvided_returnsBcryptHash() {
        // Arrange
        var hashingService = new HashingServiceImpl();
        String rawPassword = "SecretPassword123!";

        // Act
        String hash = hashingService.encode(rawPassword);

        // Assert
        assertThat(hash).isNotBlank();
        assertThat(hash).startsWith("$2a$");
    }

    @Test
    void matches_whenRawPasswordMatchesHash_returnsTrue() {
        // Arrange
        var hashingService = new HashingServiceImpl();
        String rawPassword = "SecretPassword123!";
        String hash = hashingService.encode(rawPassword);

        // Act
        boolean matches = hashingService.matches(rawPassword, hash);

        // Assert
        assertThat(matches).isTrue();
    }

    @Test
    void matches_whenRawPasswordDoesNotMatchHash_returnsFalse() {
        // Arrange
        var hashingService = new HashingServiceImpl();
        String rawPassword = "SecretPassword123!";
        String wrongPassword = "WrongPassword!";
        String hash = hashingService.encode(rawPassword);

        // Act
        boolean matches = hashingService.matches(wrongPassword, hash);

        // Assert
        assertThat(matches).isFalse();
    }
}
