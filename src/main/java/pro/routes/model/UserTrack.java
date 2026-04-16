package pro.routes.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "user_tracks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTrack implements Serializable {
    @Serial
    private static final long serialVersionUID = 3L; // Обновили версию!

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    // ===== НОВОЕ: Имя автора (денормализация для ленты) =====
    @Column(name = "username")
    private String username;

    @Column(name = "name")
    private String name;

    // ===== НОВОЕ ПОЛЕ: Тип тренировки =====
    @Column(name = "activity_type")
    private String activityType; // "WALKING", "RUNNING", "HIKING", "CYCLING"

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "duration_sec")
    private Long durationSec;

    @Column(name = "gpx_url")
    private String gpxUrl;

    // ===== НОВОЕ: Фотографии =====
    @OneToMany(mappedBy = "userTrack", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @Builder.Default
    private List<UserTrackImage> images = new ArrayList<>();

    // ===== НОВОЕ: Количество лайков (кэшированное) =====
    @Column(name = "like_count", nullable = false)
    @Builder.Default
    private Integer likeCount = 0;

    // ===== НОВОЕ: Количество комментариев (кэшированное) =====
    @Column(name = "comment_count", nullable = false)
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
