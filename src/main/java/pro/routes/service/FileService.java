package pro.routes.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;
    private final String bucketName = "shyn-images";

    /**
     * Теперь принимает fullPath, например: "route-15/uuid_photo.jpg"
     */
    public void uploadFile(MultipartFile file, String fullPath) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fullPath)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );
    }

    /**
     * Удаляет файл, вырезая путь из полного публичного URL
     */
    public void deleteFile(String imageUrl) {
        try {
            // Если URL: http://.../shyn-images/route-15/file.jpg
            // Нам нужно вырезать "route-15/file.jpg"
            String objectPath = imageUrl.substring(imageUrl.indexOf(bucketName) + bucketName.length() + 1);

            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(objectPath)
                            .build()
            );
        } catch (Exception e) {
            System.err.println("Ошибка при удалении файла из MinIO: " + e.getMessage());
        }
    }
}