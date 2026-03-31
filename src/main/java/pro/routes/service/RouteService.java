package pro.routes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    // Сюда прилетит значение из docker-compose (или localhost по умолчанию)
    @Value("${MINIO_PUBLIC_URL:http://localhost:9000}")
    private String minioPublicUrl;

    // 1. Этот метод нужен для твоего GET /api/routes
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    // 2. Основной метод создания с загрузкой фото
    public Route createRoute(Route route, MultipartFile file) throws Exception {
        // Получаем имя файла из FileService
        String fileName = fileService.uploadFile(file);

        // Собираем полный URL для базы
        String publicImageUrl = minioPublicUrl + "/shyn-images/" + fileName;
        route.setImageUrl(publicImageUrl);

        return routeRepository.save(route);
    }

    // 3. Этот метод нужен для GET /api/routes/{id}
    public Route getRouteById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Маршрут с id " + id + " не найден"));
    }
}