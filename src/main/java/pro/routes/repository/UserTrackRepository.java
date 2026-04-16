package pro.routes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;
import pro.routes.model.UserTrack;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserTrackRepository extends JpaRepository<UserTrack, Long> {

    List<UserTrack> findByUserId(Long userId);

    // Загружаем треки с картинками за один запрос (без N+1 проблемы)
    @EntityGraph(attributePaths = {"images"})
    List<UserTrack> findAllByOrderByCreatedAtDesc();

    @EntityGraph(attributePaths = {"images"})
    List<UserTrack> findByUserIdOrderByCreatedAtDesc(Long userId);

    @EntityGraph(attributePaths = {"images"})
    Optional<UserTrack> findWithImagesById(Long id);
}
