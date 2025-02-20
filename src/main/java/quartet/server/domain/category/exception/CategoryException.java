package quartet.server.domain.category.exception;

import quartet.server.core.code.ResponseCode;
import quartet.server.core.exception.BaseException;

public class CategoryException extends BaseException {
    protected CategoryException(ResponseCode errorCode) {
        super(errorCode);
    }
}
