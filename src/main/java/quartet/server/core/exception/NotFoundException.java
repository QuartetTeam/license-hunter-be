package quartet.server.core.exception;

import lombok.Getter;
import quartet.server.core.code.ErrorCode;

@Getter
public class NotFoundException extends CommonException {
    public NotFoundException(final ErrorCode errorCode) {
        super(errorCode);
    }
}