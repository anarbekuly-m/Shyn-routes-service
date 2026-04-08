package pro.routes.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.routes.model.Route;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    // Здесь уже есть методы findAll, save, delete и т.д.
    @EntityGraph(attributePaths = {"images"})
    List<Route> findAll();
}