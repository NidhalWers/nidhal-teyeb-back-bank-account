package exalt.kata.acceptance.client;

import exalt.kata.acceptance.AcceptanceTestBase;
import exalt.kata.application.contracts.AddressDTO;
import exalt.kata.application.contracts.publicCommands.request.CreateClientCommandRequest;
import exalt.kata.application.contracts.publicCommands.response.CreateClientCommandResponse;
import exalt.kata.application.primitives.IPublicCommandHandler;
import exalt.kata.domain.client.ClientStatus;
import exalt.kata.domain.core.primitives.ErrorCode;
import exalt.kata.domain.core.primitives.PublicError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

public class CreateClientCommandHandlerTest extends AcceptanceTestBase
{
    @Autowired
    private IPublicCommandHandler<CreateClientCommandRequest, CreateClientCommandResponse, PublicError> createClientCommandHandler;

    @Test
    public void withoutLastName_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreateClientCommandRequestBuilder.builder()
            .withLastName(null)
            .build();

        // Act
        var commandResponseResult = createClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The lastName field is required");
    }

    @Test
    public void withoutFirstName_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreateClientCommandRequestBuilder.builder()
            .withFirstName(null)
            .build();

        // Act
        var commandResponseResult = createClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The firstName field is required");
    }

    @Test
    public void withoutBirthDate_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreateClientCommandRequestBuilder.builder()
            .withBirthDate(null)
            .build();

        // Act
        var commandResponseResult = createClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The birthDate field is required");
    }

    @Test
    public void withoutAddress_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var commandRequest = CreateClientCommandRequestBuilder.builder()
            .withAddress(null)
            .build();

        // Act
        var commandResponseResult = createClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("The address field is required");
    }

    @Test
    public void withBlankAddressCity_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var addressDTO = new AddressDTO("addresseLine1", "addresseLine2", " ", "IDF", "75009", "FR");
        var commandRequest = CreateClientCommandRequestBuilder.builder()
            .withAddress(addressDTO)
            .build();

        // Act
        var commandResponseResult = createClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("city should not be blank");
    }

    @Test
    public void withInvalidAddressCountryCode_ShouldBeFailed_AndReturnsParamError()
    {
        // Arrange
        var addressDTO = new AddressDTO("addresseLine1", "addresseLine2", "Paris", "IDF", "75009", "XYZ");
        var commandRequest = CreateClientCommandRequestBuilder.builder()
            .withAddress(addressDTO)
            .build();

        // Act
        var commandResponseResult = createClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.param_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("XYZ is not a valid CountryCode");
    }

    @Test
    public void withBirthDateLessThan16YearsAgo_ShouldBeFailed_AndReturnsClientError()
    {
        // Arrange
        var commandRequest = CreateClientCommandRequestBuilder.builder()
            .withBirthDate(LocalDate.now().minusYears(10))
            .build();

        // Act
        var commandResponseResult = createClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isFailed()).isTrue();
        assertThat(commandResponseResult.publicError().errorCode()).isEqualTo(ErrorCode.client_error);
        assertThat(commandResponseResult.getFirstErrorMessage()).isEqualTo("Client must be at least 16 years old");
    }

    @Test
    public void nominalCase_ShouldBeSucceeded_CreatedClient()
    {
        // Arrange
        var commandRequest = CreateClientCommandRequestBuilder.builder().build();

        // Act
        var commandResponseResult = createClientCommandHandler.handleCommand(commandRequest);

        // Assert
        assertThat(commandResponseResult.isSuccess()).isTrue();
        assertThat(commandResponseResult.value()).isNotNull();
        assertThat(commandResponseResult.value().status()).isEqualTo(ClientStatus.CREATED.name());
    }
}
