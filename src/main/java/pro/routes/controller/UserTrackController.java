package pro.routes.controller;

import jakarta.servlet.http.HttpServletRequest; // Важно для работы с заголовками напрямую
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.model.UserTrack;
import pro.routes.service.UserTrackService;
import pro.routes.service.JwtService;

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
            HttpServletRequest request, // Spring сам подставит объект запроса
            @RequestPart("track") String trackJson,
            @RequestPart(value = "file", required = false) MultipartFile gpxFile
    ) throws Exception {

        Long userId = extractUserId(request);
        return ResponseEntity.ok(userTrackService.saveTrack(trackJson, gpxFile, userId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<UserTrack>> getMyTracks(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(userTrackService.getTracksByUserId(userId));
    }

    // Выносим логику извлечения, чтобы не дублировать код
    private Long extractUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring(7);
        return jwtService.extractUserId(token);
    }
}