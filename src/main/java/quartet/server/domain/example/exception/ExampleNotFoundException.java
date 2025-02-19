package quartet.server.domain.example.exception;

import quartet.server.core.code.ExampleErrorCode;

public class ExampleNotFoundException extends ExampleException {
    public ExampleNotFoundException() {
        super(ExampleErrorCode.EXAMPLE_NOT_FOUND);
    }
}