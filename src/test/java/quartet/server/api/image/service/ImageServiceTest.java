package quartet.server.api.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;
import quartet.server.api.image.fixture.ImageFixture;
import quartet.server.domain.image.exception.ImageDeletionFailedException;
import quartet.server.domain.image.exception.ImageUploadFailedException;
import quartet.server.domain.image.exception.InvalidImageUrlFormatException;
import quartet.server.domain.image.service.ImageService;

import java.io.IOException;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ImageServiceTest {

    @Mock
    private AmazonS3 amazonS3;

    @InjectMocks
    private ImageService imageService;

    private final String BUCKET_NAME = "test-bucket";

    @Nested
    @DisplayName("이미지 업로드")
    class UploadImageTest {

        @Test
        @DisplayName("이미지를 성공적으로 업로드한다")
        void success_uploadImage_shouldReturnImageUrl() throws Exception {
            // given
            ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
            final MultipartFile mockFile = ImageFixture.createMockMultipartFile();
            final URL mockUrl = ImageFixture.createMockUrl();

            when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(null);
            when(amazonS3.getUrl(eq(BUCKET_NAME), any(String.class))).thenReturn(mockUrl);

            // when
            final String result = imageService.uploadImage(mockFile);

            // then
            assertThat(result).isEqualTo(mockUrl.toString());
            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
            verify(amazonS3, times(1)).getUrl(eq(BUCKET_NAME), any(String.class));
        }

        @Test
        @DisplayName("파일 입출력 오류로 이미지 업로드에 실패한다")
        void fail_uploadImage_whenIOExceptionOccurs() throws Exception {
            // given
            ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
            final MultipartFile mockFile = mock(MultipartFile.class);

            when(mockFile.getOriginalFilename()).thenReturn("test-image.jpg");
            when(mockFile.getContentType()).thenReturn("image/jpeg");
            when(mockFile.getSize()).thenReturn(1024L);
            when(mockFile.getInputStream()).thenThrow(new IOException("파일 입출력 오류"));

            // when & then
            assertThatThrownBy(() -> imageService.uploadImage(mockFile))
                    .isInstanceOf(ImageUploadFailedException.class);

            verify(amazonS3, never()).putObject(any(PutObjectRequest.class));
        }

        @Test
        @DisplayName("S3 서비스 오류로 이미지 업로드에 실패한다")
        void fail_uploadImage_whenAmazonS3ExceptionOccurs() throws Exception {
            // given
            ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
            final MultipartFile mockFile = ImageFixture.createMockMultipartFile();

            when(amazonS3.putObject(any(PutObjectRequest.class)))
                    .thenThrow(new AmazonS3Exception("S3 서비스 오류"));

            // when & then
            assertThatThrownBy(() -> imageService.uploadImage(mockFile))
                    .isInstanceOf(ImageUploadFailedException.class);

            verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
        }
    }
    @Nested
    @DisplayName("이미지 업데이트")
    class UpdateImageTest {

        @Test
        @DisplayName("이미지를 성공적으로 업데이트한다")
        void success_updateImage_shouldReturnNewImageUrl() throws Exception {
            // given
            ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
            final String oldImageUrl = ImageFixture.getTestImageUrl();
            final MultipartFile newFile = ImageFixture.createMockMultipartFile();
            final URL mockNewUrl =ImageFixture.createMockNewUrl();

            ImageService spyImageService = spy(imageService);
            doReturn(mockNewUrl.toString()).when(spyImageService).uploadImage(any(MultipartFile.class));

            doNothing().when(spyImageService).deleteImage(anyString());

            // when
            final String result = spyImageService.updateImage(oldImageUrl, newFile);

            // then
            assertThat(result).isEqualTo(mockNewUrl.toString());
            verify(spyImageService, times(1)).uploadImage(any(MultipartFile.class));
            verify(spyImageService, times(1)).deleteImage(oldImageUrl);
        }

        @Test
        @DisplayName("이전 이미지 URL이 null일 때 성공적으로 새 이미지를 업로드한다")
        void success_updateImage_whenOldImageUrlIsNull() throws Exception {
            // given
            ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
            final String oldImageUrl = null;
            final MultipartFile newFile = ImageFixture.createMockMultipartFile();
            final URL mockNewUrl = ImageFixture.createMockNewUrl();

            ImageService spyImageService = spy(imageService);
            doReturn(mockNewUrl.toString()).when(spyImageService).uploadImage(any(MultipartFile.class));

            // when
            final String result = spyImageService.updateImage(oldImageUrl, newFile);

            // then
            assertThat(result).isEqualTo(mockNewUrl.toString());
            verify(spyImageService, times(1)).uploadImage(any(MultipartFile.class));
            verify(spyImageService, never()).deleteImage(anyString());
        }

        @Test
        @DisplayName("이전 이미지 URL이 빈 문자열일 때 성공적으로 새 이미지를 업로드한다")
        void success_updateImage_whenOldImageUrlIsEmpty() throws Exception {
            // given
            ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
            final String oldImageUrl = "";
            final MultipartFile newFile = ImageFixture.createMockMultipartFile();
            final URL mockNewUrl = ImageFixture.createMockNewUrl();

            ImageService spyImageService = spy(imageService);
            doReturn(mockNewUrl.toString()).when(spyImageService).uploadImage(any(MultipartFile.class));

            // when
            final String result = spyImageService.updateImage(oldImageUrl, newFile);

            // then
            assertThat(result).isEqualTo(mockNewUrl.toString());
            verify(spyImageService, times(1)).uploadImage(any(MultipartFile.class));
            verify(spyImageService, never()).deleteImage(anyString());
        }
    }

    @Nested
    @DisplayName("이미지 삭제")
    class DeleteImageTest {

        @Test
        @DisplayName("이미지를 성공적으로 삭제한다")
        void success_deleteImage() {
            // given
            ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
            final String imageUrl = ImageFixture.getTestImageUrl();

            doNothing().when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

            // when
            imageService.deleteImage(imageUrl);

            // then
            verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class));
        }

        @Test
        @DisplayName("S3 서비스 오류로 이미지 삭제에 실패한다")
        void fail_deleteImage_whenAmazonS3ExceptionOccurs() {
            // given
            ReflectionTestUtils.setField(imageService, "bucketName", BUCKET_NAME);
            final String imageUrl = ImageFixture.getTestImageUrl();

            doThrow(new AmazonS3Exception("S3 서비스 오류"))
                    .when(amazonS3).deleteObject(any(DeleteObjectRequest.class));

            // when & then
            assertThatThrownBy(() -> imageService.deleteImage(imageUrl))
                    .isInstanceOf(ImageDeletionFailedException.class);

            verify(amazonS3, times(1)).deleteObject(any(DeleteObjectRequest.class));
        }
    }

    @Nested
    @DisplayName("URL에서 파일명 추출")
    class ExtractFileNameFromUrlTest {

        @Test
        @DisplayName("URL에서 파일명을 성공적으로 추출한다")
        void success_extractFileNameFromUrl() {
            // given
            final String imageUrl = ImageFixture.getTestImageUrl();
            final String expectedFileName = "images/uuid_test-image.jpg";

            // when
            final String result = ReflectionTestUtils.invokeMethod(
                    imageService,
                    "extractFileNameFromUrl",
                    imageUrl
            );

            // then
            assertThat(result).isEqualTo(expectedFileName);
        }

        @Test
        @DisplayName("유효한 S3 URL에서 파일명을 성공적으로 추출한다")
        void success_extractFileNameFromUrl_withValidUrl() {
            // given
            final String validUrl = "https://test-bucket.s3.amazonaws.com/images/uuid_test-image.jpg";
            final String expectedPath = "images/uuid_test-image.jpg";

            // when
            final String result = ReflectionTestUtils.invokeMethod(
                    imageService,
                    "extractFileNameFromUrl",
                    validUrl
            );

            // then
            assertThat(result).isEqualTo(expectedPath);
        }

        @Test
        @DisplayName("잘못된 URL 형식으로 파일명 추출에 실패한다 - 스키마 없음")
        void fail_extractFileNameFromUrl_whenNoScheme() {
            // given
            final String invalidUrl = "test-bucket.s3.amazonaws.com/images/test.jpg";

            // when & then
            assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                    imageService,
                    "extractFileNameFromUrl",
                    invalidUrl
            )).isInstanceOf(InvalidImageUrlFormatException.class);
        }

        @Test
        @DisplayName("잘못된 URL 형식으로 파일명 추출에 실패한다 - 호스트 없음")
        void fail_extractFileNameFromUrl_whenNoHost() {
            // given
            final String invalidUrl = "https:///images/test.jpg";

            // when & then
            assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                    imageService,
                    "extractFileNameFromUrl",
                    invalidUrl
            )).isInstanceOf(InvalidImageUrlFormatException.class);
        }

        @Test
        @DisplayName("잘못된 URL 형식으로 파일명 추출에 실패한다 - 경로 없음")
        void fail_extractFileNameFromUrl_whenNoPath() {
            // given
            final String invalidUrl = "https://test-bucket.s3.amazonaws.com";

            // when & then
            assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                    imageService,
                    "extractFileNameFromUrl",
                    invalidUrl
            )).isInstanceOf(InvalidImageUrlFormatException.class);
        }

        @Test
        @DisplayName("잘못된 URL 형식으로 파일명 추출에 실패한다 - 구문 오류")
        void fail_extractFileNameFromUrl_whenSyntaxError() {
            // given
            final String invalidUrl = "http://exa mple.com/test.jpg";

            // when & then
            assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                    imageService,
                    "extractFileNameFromUrl",
                    invalidUrl
            )).isInstanceOf(InvalidImageUrlFormatException.class);
        }

        @Test
        @DisplayName("null URL로 파일명 추출에 실패한다")
        void fail_extractFileNameFromUrl_whenUrlIsNull() {
            // when & then
            assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                    imageService,
                    "extractFileNameFromUrl",
                    (String) null
            )).isInstanceOf(InvalidImageUrlFormatException.class);
        }

        @Test
        @DisplayName("빈 문자열 URL로 파일명 추출에 실패한다")
        void fail_extractFileNameFromUrl_whenUrlIsEmpty() {
            // given
            final String emptyUrl = "";

            // when & then
            assertThatThrownBy(() -> ReflectionTestUtils.invokeMethod(
                    imageService,
                    "extractFileNameFromUrl",
                    emptyUrl
            )).isInstanceOf(InvalidImageUrlFormatException.class);
        }
    }
}