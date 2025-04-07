package quartet.server.domain.image.exception;

import quartet.server.core.code.ImageErrorCode;

public class ImageDeletionFailedException extends ImageException {
    public ImageDeletionFailedException() {
        super(ImageErrorCode.IMAGE_DELETION_FAILED);
    }
}