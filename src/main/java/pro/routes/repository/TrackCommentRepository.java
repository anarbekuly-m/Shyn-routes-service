package pro.routes.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.routes.model.TrackComment;

import java.util.List;

@Repository
public interface TrackCommentRepository extends JpaRepository<TrackComment, Long> {

    List<TrackComment> findByUserTrackIdOrderByCreatedAtAsc(Long userTrackId);
}
