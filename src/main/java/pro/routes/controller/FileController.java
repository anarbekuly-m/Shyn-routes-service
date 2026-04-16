package pro.routes.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.service.FileService;
import pro.routes.service.JwtService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FileController {

    private final FileService fileService;
    private final JwtService jwtService;

    /**
     * Загрузить одну или несколько фотографий.
     * Возвращает массив готовых публичных URL.
     *
     * Мобильщик может вызвать этот эндпоинт для каждого фото отдельно,
     * или отправить все сразу — как ему удобнее.
     *
     * POST /upload/photos
     * Content-Type: multipart/form-data
     * Authorization: Bearer <token>
     *
     * Ответ: ["https://shyn-api.site/shyn-images/tracks/7/uuid_photo.jpg", ...]
     */
    @PostMapping(value = "/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<List<String>> uploadPhotos(
            HttpServletRequest request,
            @RequestPart("files") List<MultipartFile> files
    ) throws Exception {

        Long userId = extractUserId(request);
        List<String> urls = new ArrayList<>();

        for (MultipartFile file : files) {
            String fileName = "tracks/" + userId + "/" + UUID.randomUUID() + "_" + file.getOriginalFilename();
            String url = fileService.uploadFile(file, fileName);
            urls.add(url);
        }

        return ResponseEntity.ok(urls);
    }

    /**
     * Загрузить GPX файл.
     *
     * POST /upload/gpx
     * Content-Type: multipart/form-data
     * Authorization: Bearer <token>
     *
     * Ответ: "https://shyn-api.site/shyn-images/tracks/7/uuid.gpx"
     */
    @PostMapping(value = "/gpx", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> uploadGpx(
            HttpServletRequest request,
            @RequestPart("file") MultipartFile file
    ) throws Exception {

        Long userId = extractUserId(request);
        String fileName = "tracks/" + userId + "/" + UUID.randomUUID() + ".gpx";
        String url = fileService.uploadFile(file, fileName);

        return ResponseEntity.ok(url);
    }

    private Long extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return jwtService.extractUserId(authHeader.substring(7));
    }
}
