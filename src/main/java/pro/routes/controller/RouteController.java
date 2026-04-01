package pro.routes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.model.Route;
import pro.routes.service.RouteService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RouteController {

    private final RouteService routeService;

    // 1. Получить все маршруты
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    // 2. Создать новый маршрут (Универсальный метод для Swagger и Postman)
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Route> createRoute(
            @RequestPart("route")
            @Parameter(schema = @Schema(implementation = Route.class))
            String routeJson,

            @RequestPart("file")
            MultipartFile file
    ) throws Exception {
        // Ручная десериализация JSON строки в объект Route
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules(); // Поддержка дат и спец. типов

        Route route = objectMapper.readValue(routeJson, Route.class);

        return ResponseEntity.ok(routeService.createRoute(route, file));
    }

    // 3. Получить по ID
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }
}