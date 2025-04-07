package quartet.server.domain.image.exception;

import quartet.server.core.code.ResponseCode;
import quartet.server.core.exception.BaseException;

public class ImageException extends BaseException {
    public ImageException(final ResponseCode errorCode) {
        super(errorCode);
    }
}