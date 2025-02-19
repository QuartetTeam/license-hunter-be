package quartet.server.api.example.dto.resquest;

import jakarta.validation.constraints.NotBlank;

public record ExampleRequest(@NotBlank String username) {
}