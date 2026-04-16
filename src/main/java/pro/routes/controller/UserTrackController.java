package pro.routes.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.dto.*;
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

    // =====================================================
    // 1a. СОЗДАТЬ ТРЕК — JSON (рекомендуемый для мобилки)
    //     Фото и GPX загружаются ЗАРАНЕЕ через /upload/*
    // =====================================================
    // POST /user-tracks
    // Content-Type: application/json
    // Body: { "name": "...", "activityType": "HIKING", "imageUrls": [...], "gpxUrl": "..." }
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<TrackFeedResponse> createTrackJson(
            @RequestBody CreateTrackRequest request,
            HttpServletRequest httpRequest
    ) {
        Long userId = extractUserId(httpRequest);
        String username = extractUsername(httpRequest);
        return ResponseEntity.ok(userTrackService.saveTrackFromJson(request, userId, username));
    }

    // =====================================================
    // 1b. СОЗДАТЬ ТРЕК — Multipart (всё в одном запросе)
    //     Альтернативный вариант, если мобильщику удобнее
    // =====================================================
    // POST /user-tracks
    // Content-Type: multipart/form-data
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<TrackFeedResponse> createTrackMultipart(
            HttpServletRequest request,
            @RequestPart("track") String trackJson,
            @RequestPart(value = "file", required = false) MultipartFile gpxFile,
            @RequestPart(value = "photos", required = false) List<MultipartFile> photos
    ) throws Exception {
        Long userId = extractUserId(request);
        String username = extractUsername(request);
        return ResponseEntity.ok(userTrackService.saveTrackMultipart(trackJson, gpxFile, photos, userId, username));
    }

    // =====================================================
    // 2. МОИ ТРЕКИ (Profile tab)
    // =====================================================
    @GetMapping("/my")
    public ResponseEntity<List<TrackFeedResponse>> getMyTracks(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(userTrackService.getTracksByUserId(userId));
    }

    // =====================================================
    // 3. ЛЕНТА ВСЕХ ТРЕКОВ (Community tab)
    // =====================================================
    @GetMapping("/feed")
    public ResponseEntity<List<TrackFeedResponse>> getFeed(HttpServletRequest request) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(userTrackService.getFeed(userId));
    }

    // =====================================================
    // 4. ПОЛУЧИТЬ ОДИН ТРЕК (Detail view)
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<TrackFeedResponse> getTrackById(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(userTrackService.getTrackById(id, userId));
    }

    // =====================================================
    // 5. ЛАЙКНУТЬ ТРЕК
    // =====================================================
    @PostMapping("/{id}/like")
    public ResponseEntity<TrackFeedResponse> likeTrack(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(userTrackService.likeTrack(id, userId));
    }

    // =====================================================
    // 6. УБРАТЬ ЛАЙК
    // =====================================================
    @DeleteMapping("/{id}/like")
    public ResponseEntity<TrackFeedResponse> unlikeTrack(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = extractUserId(request);
        return ResponseEntity.ok(userTrackService.unlikeTrack(id, userId));
    }

    // =====================================================
    // 7. ДОБАВИТЬ КОММЕНТАРИЙ
    // =====================================================
    @PostMapping("/{id}/comments")
    public ResponseEntity<CommentResponse> addComment(
            @PathVariable Long id,
            @Valid @RequestBody CommentRequest commentRequest,
            HttpServletRequest request
    ) {
        Long userId = extractUserId(request);
        String username = extractUsername(request);
        return ResponseEntity.ok(
                userTrackService.addComment(id, userId, username, commentRequest.getText())
        );
    }

    // =====================================================
    // 8. ПОЛУЧИТЬ КОММЕНТАРИИ ТРЕКА
    // =====================================================
    @GetMapping("/{id}/comments")
    public ResponseEntity<List<CommentResponse>> getComments(@PathVariable Long id) {
        return ResponseEntity.ok(userTrackService.getComments(id));
    }

    // =====================================================
    // 9. СТАТИСТИКА ПРОФИЛЯ
    // =====================================================
    @GetMapping("/stats")
    public ResponseEntity<UserStatsResponse> getMyStats(HttpServletRequest request) {
        Long userId = extractUserId(request);
        String username = extractUsername(request);
        return ResponseEntity.ok(userTrackService.getUserStats(userId, username));
    }

    // =====================================================
    // 10. УДАЛИТЬ СВОЙ ТРЕК
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTrack(
            @PathVariable Long id,
            HttpServletRequest request
    ) {
        Long userId = extractUserId(request);
        userTrackService.deleteTrack(id, userId);
        return ResponseEntity.noContent().build();
    }

    // =====================================================
    // УТИЛИТЫ
    // =====================================================
    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        return authHeader.substring(7);
    }

    private Long extractUserId(HttpServletRequest request) {
        return jwtService.extractUserId(extractToken(request));
    }

    private String extractUsername(HttpServletRequest request) {
        return jwtService.extractUsername(extractToken(request));
    }
}
