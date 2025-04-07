package quartet.server.api.image.fixture;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;

public class ImageFixture {

    private static final String TEST_IMAGE_NAME = "test-image.jpg";
    private static final String TEST_IMAGE_CONTENT_TYPE = "image/jpeg";
    private static final byte[] TEST_IMAGE_CONTENT = "test image content".getBytes();
    private static final String TEST_IMAGE_URL = "https://test-bucket.s3.amazonaws.com/images/uuid_test-image.jpg";
    private static final String TEST_NEW_IMAGE_URL = "https://test-bucket.s3.amazonaws.com/images/new_uuid_test-image.jpg";

    public static MultipartFile createMockMultipartFile() {
        return new MockMultipartFile(
                "file",
                TEST_IMAGE_NAME,
                TEST_IMAGE_CONTENT_TYPE,
                TEST_IMAGE_CONTENT
        );
    }

    public static URL createMockUrl() throws Exception {
        return new URL(TEST_IMAGE_URL);
    }
    public static URL createMockNewUrl() throws Exception {
        return new URL(TEST_NEW_IMAGE_URL);
    }

    public static String getTestImageUrl() {
        return TEST_IMAGE_URL;
    }
}