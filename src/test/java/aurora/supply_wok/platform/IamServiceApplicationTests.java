package aurora.supply_wok.platform;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class IamServiceApplicationTests {

    @Test
    void contextLoads_whenApplicationStarts_succeeds() {
        // Arrange
        boolean contextLoaded = true;

        // Act & Assert
        assertThat(contextLoaded).isTrue();
    }
}
