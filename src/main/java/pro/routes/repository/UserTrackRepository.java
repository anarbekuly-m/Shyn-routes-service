package pro.routes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.routes.model.UserTrack;

import java.util.List;

@Repository
public interface UserTrackRepository extends JpaRepository<UserTrack, Long> {
    List<UserTrack> findByUserId(Long userId);
}