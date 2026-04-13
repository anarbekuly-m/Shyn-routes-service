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

    // Базовый URL твоего MinIO (замени на свой, если он другой)
    private final String baseUrl = "https://shyn-api.site/shyn-images/";

    /**
     * Загружает файл и возвращает полный публичный URL
     */
    public String uploadFile(MultipartFile file, String fullPath) throws Exception {
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fullPath)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        // Возвращаем путь, чтобы сохранить его в БД
        return baseUrl + fullPath;
    }

    /**
     * Удаляет файл, вырезая путь из полного публичного URL
     */
    public void deleteFile(String imageUrl) {
        try {
            // Если URL: http://.../shyn-images/route-15/file.jpg
            // Нам нужно вырезать "route-15/file.jpg"
            if (imageUrl == null || !imageUrl.contains(bucketName)) return;

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