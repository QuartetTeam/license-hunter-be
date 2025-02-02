package quartet.server.api.test.dto.resquest;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TestRequest {
    @NotEmpty
    private String username;
}
