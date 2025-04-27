package quartet.server.domain.image.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import quartet.server.domain.image.exception.ImageDeletionFailedException;
import quartet.server.domain.image.exception.ImageUploadFailedException;
import quartet.server.domain.image.exception.InvalidImageUrlFormatException;
import quartet.server.domain.image.exception.UnsupportedImageFormatException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ImageService {
    private final AmazonS3 amazonS3;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private final String IMAGE_FOLDER = "images/";

    public String uploadImage(MultipartFile file) {
        validateImageFile(file);

        try {
            String fileName = IMAGE_FOLDER + UUID.randomUUID() + "_" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(file.getContentType());
            metadata.setContentLength(file.getSize());

            amazonS3.putObject(new PutObjectRequest(
                    bucketName, fileName, file.getInputStream(), metadata));

            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            log.error("[IOException] : 파일 입출력 오류 - {}", e.getMessage(), e);
            throw new ImageUploadFailedException();
        } catch (AmazonS3Exception e) {
            log.error("[AmazonS3Exception] : S3 서비스 오류 - {}", e.getMessage(), e);
            throw new ImageUploadFailedException();
        } catch (Exception e) {
            log.error("[Exception] : 이미지 업로드 중 예상치 못한 오류 - {}", e.getMessage(), e);
            throw new ImageUploadFailedException();
        }
    }

    public String uploadImageFromUrl(String imageUrl) {
        try (InputStream inputStream = new URL(imageUrl).openStream()) {
            String fileName = IMAGE_FOLDER + "profile_" + UUID.randomUUID() + ".jpg";

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("image/jpeg");

            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, inputStream, metadata));

            return amazonS3.getUrl(bucketName, fileName).toString();
        } catch (IOException e) {
            log.error("[IOException] : 프로필 이미지 URL 읽기 실패 - {}", e.getMessage(), e);
            throw new ImageUploadFailedException();
        } catch (AmazonS3Exception e) {
            log.error("[AmazonS3Exception] : S3 업로드 실패 - {}", e.getMessage(), e);
            throw new ImageUploadFailedException();
        } catch (Exception e) {
            log.error("[Exception] : 프로필 이미지 업로드 중 예상치 못한 오류 - {}", e.getMessage(), e);
            throw new ImageUploadFailedException();
        }
    }

    public String updateImage(String oldImageUrl, MultipartFile newFile) {
        String newImageUrl = uploadImage(newFile);

        if (oldImageUrl != null && !oldImageUrl.isEmpty()) {
            deleteImage(oldImageUrl);
        }

        return newImageUrl;
    }

    public void deleteImage(String oldImageUrl) {
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, extractFileNameFromUrl(oldImageUrl)));
        }
        catch (AmazonS3Exception e) {
            log.error("[AmazonS3Exception] : S3 서비스 오류 - {}", e.getMessage(), e);
            throw new ImageDeletionFailedException();
        } catch (Exception e) {
            log.error("[Exception] : 이미지 삭제 중 예상치 못한 오류 - {}", e.getMessage(), e);
            throw new ImageDeletionFailedException();
        }
    }

    private String extractFileNameFromUrl(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String path = uri.getPath();

            if (uri.getScheme() == null || uri.getHost() == null || uri.getPath() == null) {
                throw new InvalidImageUrlFormatException();
            }

            if (path.startsWith("/")) {
                path = path.substring(1);
            }else{
                throw new InvalidImageUrlFormatException();
            }

            return path;
        } catch (Exception e) {
            throw new InvalidImageUrlFormatException();
        }
    }

    private void validateImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new UnsupportedImageFormatException();
        }
    }
}