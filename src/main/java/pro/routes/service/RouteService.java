package pro.routes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.model.Route;
import pro.routes.repository.RouteRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;
    private final FileService fileService;

    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    public Route createRoute(Route route, MultipartFile file) throws Exception {
        // 1. Загружаем файл в MinIO и получаем его имя/путь
        String imagePath = fileService.uploadFile(file);

        // 2. Устанавливаем путь к картинке в модель
        // Здесь можно сразу формировать полный URL или только путь
        route.setImageUrl(imagePath);

        // 3. Сохраняем всё в PostgreSQL
        return routeRepository.save(route);
    }

    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Маршрут с id " + id + " не найден"));
    }
}