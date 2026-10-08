package aurora.supply_wok.platform.profiles.domain.model.valueobjects;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EProfileTypeTests {

    @Test
    void fromPath_whenValidLowercase_returnsEnumValue() {
        // Arrange
        String value = "restaurant";

        // Act
        var type = EProfileType.fromPath(value);

        // Assert
        assertThat(type).isEqualTo(EProfileType.RESTAURANT);
    }

    @Test
    void fromPath_whenValidSupplierWithSpaces_trimsAndReturnsEnum() {
        // Arrange
        String value = "  SUPPLIER  ";

        // Act
        var type = EProfileType.fromPath(value);

        // Assert
        assertThat(type).isEqualTo(EProfileType.SUPPLIER);
    }

    @Test
    void fromPath_whenNull_throwsIllegalArgumentException() {
        // Arrange
        String value = null;

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> EProfileType.fromPath(value));
    }

    @Test
    void fromPath_whenBlank_throwsIllegalArgumentException() {
        // Arrange
        String value = "   ";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> EProfileType.fromPath(value));
    }

    @Test
    void fromPath_whenInvalidValue_throwsIllegalArgumentException() {
        // Arrange
        String value = "INVALID_TYPE";

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> EProfileType.fromPath(value));
    }
}
