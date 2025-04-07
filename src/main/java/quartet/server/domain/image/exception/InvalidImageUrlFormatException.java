package quartet.server.domain.image.exception;

import quartet.server.core.code.ImageErrorCode;

public class InvalidImageUrlFormatException extends ImageException {
    public InvalidImageUrlFormatException() {
        super(ImageErrorCode.INVALID_IMAGE_URL_FORMAT);
    }
}