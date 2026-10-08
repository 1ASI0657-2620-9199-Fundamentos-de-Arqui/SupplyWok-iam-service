package aurora.supply_wok.platform.shared.interfaces.rest.transform;

import aurora.supply_wok.platform.shared.application.result.ApplicationError;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ErrorResponseAssemblerTests {

    @Test
    void toStatusFromErrorCode_whenErrorCodesProvided_mapsToExpectedHttpStatus() {
        // Arrange
        String validation = "VALIDATION_ERROR";
        String notFound = "USER_NOT_FOUND";
        String conflict = "USER_CONFLICT";
        String rule = "BUSINESS_RULE_VIOLATION";
        String unexpected = "UNEXPECTED_ERROR";
        String unknown = "UNKNOWN_CODE";

        // Act & Assert
        assertThat(ErrorResponseAssembler.toStatusFromErrorCode(validation)).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ErrorResponseAssembler.toStatusFromErrorCode(notFound)).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ErrorResponseAssembler.toStatusFromErrorCode(conflict)).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ErrorResponseAssembler.toStatusFromErrorCode(rule).value()).isEqualTo(422);
        assertThat(ErrorResponseAssembler.toStatusFromErrorCode(unexpected)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(ErrorResponseAssembler.toStatusFromErrorCode(unknown)).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @Test
    void toErrorResponseFromApplicationError_whenErrorGiven_returnsResponseEntityWithErrorResource() {
        // Arrange
        var error = ApplicationError.validationError("email", "Email cannot be blank");

        // Act
        var response = ErrorResponseAssembler.toErrorResponseFromApplicationError(error);

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().details()).isEqualTo("Email cannot be blank");
    }
}
