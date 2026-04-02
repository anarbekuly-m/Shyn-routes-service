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
@CrossOrigin(origins = "*") // Хорошая практика добавить базовый путь
public class RouteController {

    private final RouteService routeService;

    // 1. Получить все маршруты
    @GetMapping
    public ResponseEntity<List<Route>> getAllRoutes() {
        return ResponseEntity.ok(routeService.getAllRoutes());
    }

    // 2. Создать новый маршрут со стеком фото
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Route> createRoute(
            @RequestPart("route")
            @Parameter(schema = @Schema(implementation = Route.class))
            String routeJson,

            @RequestPart("files") // Изменено на files
            List<MultipartFile> files
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();

        Route route = objectMapper.readValue(routeJson, Route.class);

        return ResponseEntity.ok(routeService.createRoute(route, files));
    }

    // 3. Получить по ID
    @GetMapping("/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable Long id) {
        return ResponseEntity.ok(routeService.getRouteById(id));
    }

    // 4. Обновить маршрут (можно добавить новые фото в стек)
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Route> updateRoute(
            @PathVariable Long id,
            @RequestPart("route")
            @Parameter(schema = @Schema(implementation = Route.class)) String routeJson,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.findAndRegisterModules();
        Route routeDetails = objectMapper.readValue(routeJson, Route.class);

        return ResponseEntity.ok(routeService.updateRoute(id, routeDetails, files));
    }

    // 5. Удалить маршрут
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return ResponseEntity.noContent().build();
    }
}