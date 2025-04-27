package quartet.server.domain.image.exception;

import quartet.server.core.code.ImageErrorCode;

public class UnsupportedImageFormatException extends ImageException{
    public UnsupportedImageFormatException() { super(ImageErrorCode.UNSUPPORTED_IMAGE_FORMAT); }
}
