package pro.routes.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime; // Нужно импортировать

@Entity
@Table(name = "user_tracks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor // Хорошая практика для билдеров и тестов
@Builder // Пригодится для удобного создания объекта в сервисе
public class UserTrack implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L; // Уникальный ID для этой версии класса

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "distance_km")
    private Double distanceKm;

    @Column(name = "duration_sec")
    private Long durationSec;

    @Column(name = "gpx_url")
    private String gpxUrl;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
}