package quartet.server.domain.image.exception;

import quartet.server.core.code.ImageErrorCode;

public class ImageUploadFailedException extends ImageException {
    public ImageUploadFailedException() {
        super(ImageErrorCode.IMAGE_UPLOAD_FAILED);
    }
}