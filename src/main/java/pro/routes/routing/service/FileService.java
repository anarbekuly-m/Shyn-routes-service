package pro.routes.routing.service;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.http.Method;
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
        // 1. Генерируем уникальное имя файла, чтобы не перезаписать старые фото
        String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();

        // 2. Загружаем файл в бакет
        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        // 3. Возвращаем URL, по которому Flutter сможет открыть фото
        // В продакшене тут будет твой домен shyn-api.site
        return "http://localhost:9000/" + bucketName + "/" + fileName;
    }
}