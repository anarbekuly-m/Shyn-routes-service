package pro.routes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.routes.model.TrackLike;

import java.util.List;
import java.util.Optional;

@Repository
public interface TrackLikeRepository extends JpaRepository<TrackLike, Long> {

    // Проверить, лайкнул ли конкретный юзер конкретный трек
    boolean existsByUserIdAndUserTrackId(Long userId, Long userTrackId);

    // Найти лайк для удаления (unlike)
    Optional<TrackLike> findByUserIdAndUserTrackId(Long userId, Long userTrackId);

    // Получить все trackId, которые лайкнул юзер (для ленты — чтобы пометить сердечки)
    List<TrackLike> findByUserIdAndUserTrackIdIn(Long userId, List<Long> trackIds);
}
