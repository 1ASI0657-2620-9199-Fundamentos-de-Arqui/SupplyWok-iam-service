package aurora.supply_wok.platform.shared.interfaces.rest.transform;

import aurora.supply_wok.platform.shared.application.result.ApplicationError;
import aurora.supply_wok.platform.shared.application.result.Result;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;

class ResponseEntityAssemblerTests {

    @Test
    void toResponseEntityFromResult_whenSuccess_returnsConfiguredStatusAndMappedBody() {
        // Arrange
        Result<String, ApplicationError> result = Result.success("sample_data");

        // Act
        var response = ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                data -> "transformed_" + data,
                HttpStatus.CREATED
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo("transformed_sample_data");
    }

    @Test
    void toResponseEntityFromResult_whenFailure_delegatesToErrorResponseAssembler() {
        // Arrange
        Result<String, ApplicationError> result = Result.failure(ApplicationError.notFound("User", "user@wok.pe"));

        // Act
        var response = ResponseEntityAssembler.toResponseEntityFromResult(
                result,
                data -> data,
                HttpStatus.OK
        );

        // Assert
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
