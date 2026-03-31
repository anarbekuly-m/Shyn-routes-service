package pro.routes.routing.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pro.routes.routing.model.Route;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    // Здесь уже есть методы findAll, save, delete и т.д.
}