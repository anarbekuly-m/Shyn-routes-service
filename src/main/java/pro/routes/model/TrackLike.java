package pro.routes.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Table(name = "track_likes",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "user_track_id"}))
// ^ Один пользователь = один лайк на трек. Нельзя лайкнуть дважды.
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackLike implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_track_id", nullable = false)
    private UserTrack userTrack;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
