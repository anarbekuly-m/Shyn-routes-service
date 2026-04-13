package pro.routes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.model.UserTrack;
import pro.routes.service.UserTrackService;
import pro.routes.service.JwtService; // Теперь импортируем локальный сервис

import java.util.List;

@RestController
@RequestMapping("/user-tracks")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserTrackController {

    private final UserTrackService userTrackService;
    private final JwtService jwtService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserTrack> createTrack(
            @RequestHeader("Authorization") String authHeader,
            @RequestPart("track") String trackJson,
            @RequestPart(value = "file", required = false) MultipartFile gpxFile
    ) throws Exception {

        // 1. Извлекаем токен из заголовка Bearer
        String token = authHeader.substring(7);

        // 2. Достаем ID (магия JWT в действии)
        Long userId = jwtService.extractUserId(token);

        // 3. Сохраняем трек с ID пользователя 17 (или любым другим)
        return ResponseEntity.ok(userTrackService.saveTrack(trackJson, gpxFile, userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserTrack>> getMyTracks(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        Long userId = jwtService.extractUserId(token);
        return ResponseEntity.ok(userTrackService.getTracksByUserId(userId));
    }
}