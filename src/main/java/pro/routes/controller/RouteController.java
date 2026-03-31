package pro.routes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.model.Route;
import pro.routes.service.RouteService;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Добавь это, чтобы Flutter (web/mobile) не ругался на CORS
public class RouteController {

    private final RouteService routeService;

    // 1. Получить список всех маршрутов для твоего Flutter-приложения
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    // 2. Создать новый маршрут (вызываем метод из сервиса, а не репозитория напрямую)
    @PostMapping(consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Route> createRoute(
            @RequestPart("route") Route route,
            @RequestPart("file") MultipartFile file
    ) throws Exception {
        // Теперь типы данных в контроллере и сервисе совпадают
        return ResponseEntity.ok(routeService.createRoute(route, file));
    }

    // 3. Получить один маршрут по ID (полезно для экрана деталей горы)
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }
}