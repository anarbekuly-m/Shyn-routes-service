package pro.routes.service;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileService {

    private final MinioClient minioClient;
    private final String bucketName = "shyn-images";

    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        // Возвращаем ТОЛЬКО имя файла, а не весь URL
        return fileName;
    }

    public void deleteFile(String imageUrl) {
        try {
            // Извлекаем имя файла из URL (все что после последнего слэша)
            String fileName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            minioClient.removeObject(
                    io.minio.RemoveObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (Exception e) {
            // Если файл не удалился, просто логируем, чтобы не ломать основной процесс
            System.err.println("Ошибка при удалении файла из MinIO: " + e.getMessage());
        }
    }
}