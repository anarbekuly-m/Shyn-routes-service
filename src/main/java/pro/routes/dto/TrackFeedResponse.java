package pro.routes.dto;

import lombok.*;
import pro.routes.model.UserTrack;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackFeedResponse {

    private Long id;
    private Long userId;
    private String username;
    private String name;
    private String activityType;
    private Double distanceKm;
    private Long durationSec;
    private String gpxUrl;
    private List<String> imageUrls;
    private Integer likeCount;
    private Integer commentCount;
    private Boolean likedByMe; // <-- Мобильщик будет знать, показать сердечко или нет
    private LocalDateTime createdAt;

    /**
     * Конвертируем Entity → DTO
     */
    public static TrackFeedResponse from(UserTrack track, boolean likedByMe) {
        return TrackFeedResponse.builder()
                .id(track.getId())
                .userId(track.getUserId())
                .username(track.getUsername())
                .name(track.getName())
                .activityType(track.getActivityType())
                .distanceKm(track.getDistanceKm())
                .durationSec(track.getDurationSec())
                .gpxUrl(track.getGpxUrl())
                .imageUrls(
                        track.getImages() != null
                                ? track.getImages().stream()
                                    .map(img -> img.getImageUrl())
                                    .collect(Collectors.toList())
                                : List.of()
                )
                .likeCount(track.getLikeCount())
                .commentCount(track.getCommentCount())
                .likedByMe(likedByMe)
                .createdAt(track.getCreatedAt())
                .build();
    }
}
