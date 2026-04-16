package pro.routes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.dto.CommentResponse;
import pro.routes.dto.TrackFeedResponse;
import pro.routes.dto.UserStatsResponse;
import pro.routes.model.TrackComment;
import pro.routes.model.TrackLike;
import pro.routes.model.UserTrack;
import pro.routes.model.UserTrackImage;
import pro.routes.repository.TrackCommentRepository;
import pro.routes.repository.TrackLikeRepository;
import pro.routes.repository.UserTrackRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserTrackService {

    private final UserTrackRepository repository;
    private final TrackLikeRepository likeRepository;
    private final TrackCommentRepository commentRepository;
    private final ObjectMapper objectMapper;
    private final FileService fileService;

    // ===================== СОЗДАНИЕ ТРЕКА =====================

    @Caching(evict = {
            @CacheEvict(value = "userTracks", key = "#userId"),
            @CacheEvict(value = "trackFeed", allEntries = true)
    })
    @Transactional
    public UserTrack saveTrack(String trackJson, MultipartFile gpxFile,
                                List<MultipartFile> photos, Long userId, String username) throws Exception {

        UserTrack track = objectMapper.readValue(trackJson, UserTrack.class);
        track.setUserId(userId);
        track.setUsername(username);
        track.setLikeCount(0);
        track.setCommentCount(0);

        UserTrack saved = repository.save(track);

        if (gpxFile != null && !gpxFile.isEmpty()) {
            String fileName = "tracks/" + userId + "/" + UUID.randomUUID() + ".gpx";
            String url = fileService.uploadFile(gpxFile, fileName);
            saved.setGpxUrl(url);
        }

        if (photos != null && !photos.isEmpty()) {
            for (MultipartFile photo : photos) {
                String fileName = "tracks/" + userId + "/" + UUID.randomUUID() + "_" + photo.getOriginalFilename();
                String url = fileService.uploadFile(photo, fileName);

                UserTrackImage image = UserTrackImage.builder()
                        .imageUrl(url)
                        .userTrack(saved)
                        .build();
                saved.getImages().add(image);
            }
        }

        log.info("Saving track for user {} ({}): name='{}', activityType='{}', photos={}",
                userId, username, saved.getName(), saved.getActivityType(),
                photos != null ? photos.size() : 0);

        return repository.save(saved);
    }

    // ===================== МОИ ТРЕКИ =====================

    @Cacheable(value = "userTracks", key = "#userId")
    public List<TrackFeedResponse> getTracksByUserId(Long userId) {
        List<UserTrack> tracks = repository.findByUserIdOrderByCreatedAtDesc(userId);

        List<Long> trackIds = tracks.stream().map(UserTrack::getId).collect(Collectors.toList());
        Set<Long> likedIds = getLikedTrackIds(userId, trackIds);

        return tracks.stream()
                .map(t -> TrackFeedResponse.from(t, likedIds.contains(t.getId())))
                .collect(Collectors.toList());
    }

    // ===================== ЛЕНТА (FEED) =====================

    @Cacheable(value = "trackFeed")
    public List<TrackFeedResponse> getFeed(Long currentUserId) {
        List<UserTrack> allTracks = repository.findAllByOrderByCreatedAtDesc();

        List<Long> trackIds = allTracks.stream().map(UserTrack::getId).collect(Collectors.toList());
        Set<Long> likedIds = getLikedTrackIds(currentUserId, trackIds);

        return allTracks.stream()
                .map(t -> TrackFeedResponse.from(t, likedIds.contains(t.getId())))
                .collect(Collectors.toList());
    }

    // ===================== ЛАЙКИ =====================

    @Caching(evict = {
            @CacheEvict(value = "trackFeed", allEntries = true),
            @CacheEvict(value = "userTracks", allEntries = true)
    })
    @Transactional
    public TrackFeedResponse likeTrack(Long trackId, Long userId) {
        UserTrack track = repository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        if (likeRepository.existsByUserIdAndUserTrackId(userId, trackId)) {
            throw new RuntimeException("Already liked");
        }

        TrackLike like = TrackLike.builder()
                .userId(userId)
                .userTrack(track)
                .build();
        likeRepository.save(like);

        track.setLikeCount(track.getLikeCount() + 1);
        repository.save(track);

        return TrackFeedResponse.from(track, true);
    }

    @Caching(evict = {
            @CacheEvict(value = "trackFeed", allEntries = true),
            @CacheEvict(value = "userTracks", allEntries = true)
    })
    @Transactional
    public TrackFeedResponse unlikeTrack(Long trackId, Long userId) {
        UserTrack track = repository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        TrackLike like = likeRepository.findByUserIdAndUserTrackId(userId, trackId)
                .orElseThrow(() -> new RuntimeException("Like not found"));

        likeRepository.delete(like);
        track.setLikeCount(Math.max(0, track.getLikeCount() - 1));
        repository.save(track);

        return TrackFeedResponse.from(track, false);
    }

    // ===================== КОММЕНТАРИИ =====================

    @Caching(evict = {
            @CacheEvict(value = "trackFeed", allEntries = true),
            @CacheEvict(value = "userTracks", allEntries = true)
    })
    @Transactional
    public CommentResponse addComment(Long trackId, Long userId, String username, String text) {
        UserTrack track = repository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        TrackComment comment = TrackComment.builder()
                .userId(userId)
                .username(username)
                .userTrack(track)
                .text(text)
                .build();

        commentRepository.save(comment);

        track.setCommentCount(track.getCommentCount() + 1);
        repository.save(track);

        return CommentResponse.from(comment);
    }

    public List<CommentResponse> getComments(Long trackId) {
        return commentRepository.findByUserTrackIdOrderByCreatedAtAsc(trackId)
                .stream()
                .map(CommentResponse::from)
                .collect(Collectors.toList());
    }

    // ===================== ПОЛУЧИТЬ ОДИН ТРЕК =====================

    public TrackFeedResponse getTrackById(Long trackId, Long currentUserId) {
        UserTrack track = repository.findWithImagesById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        boolean liked = likeRepository.existsByUserIdAndUserTrackId(currentUserId, trackId);
        return TrackFeedResponse.from(track, liked);
    }

    // ===================== СТАТИСТИКА ПРОФИЛЯ =====================

    public UserStatsResponse getUserStats(Long userId, String username) {
        List<UserTrack> tracks = repository.findByUserId(userId);

        int totalActivities = tracks.size();
        double totalDistance = tracks.stream()
                .mapToDouble(t -> t.getDistanceKm() != null ? t.getDistanceKm() : 0.0)
                .sum();
        long totalDuration = tracks.stream()
                .mapToLong(t -> t.getDurationSec() != null ? t.getDurationSec() : 0L)
                .sum();

        return UserStatsResponse.builder()
                .userId(userId)
                .username(username)
                .totalActivities(totalActivities)
                .totalDistanceKm(Math.round(totalDistance * 100.0) / 100.0)
                .totalDurationSec(totalDuration)
                .build();
    }

    // ===================== УДАЛИТЬ ТРЕК =====================

    @Caching(evict = {
            @CacheEvict(value = "trackFeed", allEntries = true),
            @CacheEvict(value = "userTracks", allEntries = true)
    })
    @Transactional
    public void deleteTrack(Long trackId, Long userId) {
        UserTrack track = repository.findById(trackId)
                .orElseThrow(() -> new RuntimeException("Track not found"));

        if (!track.getUserId().equals(userId)) {
            throw new RuntimeException("You can only delete your own tracks");
        }

        if (track.getGpxUrl() != null) {
            fileService.deleteFile(track.getGpxUrl());
        }
        if (track.getImages() != null) {
            for (UserTrackImage img : track.getImages()) {
                fileService.deleteFile(img.getImageUrl());
            }
        }

        repository.delete(track);
    }

    // ===================== УТИЛИТА =====================

    private Set<Long> getLikedTrackIds(Long userId, List<Long> trackIds) {
        if (trackIds.isEmpty()) return Set.of();
        return likeRepository.findByUserIdAndUserTrackIdIn(userId, trackIds)
                .stream()
                .map(like -> like.getUserTrack().getId())
                .collect(Collectors.toSet());
    }
}
