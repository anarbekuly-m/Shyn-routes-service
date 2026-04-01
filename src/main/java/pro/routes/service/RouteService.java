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


    // 4. Обновление маршрута
    public Route updateRoute(Long id, Route routeDetails, MultipartFile file) throws Exception {
        Route route = getRouteById(id); // Используем твой готовый метод поиска

        // Обновляем поля
        route.setName(routeDetails.getName());
        route.setLocation(routeDetails.getLocation());
        route.setDifficulty(routeDetails.getDifficulty());
        route.setDistance(routeDetails.getDistance());
        route.setCategory(routeDetails.getCategory());
        route.setLatitude(routeDetails.getLatitude());
        route.setLongitude(routeDetails.getLongitude());

        // Если прислали новый файл - обновляем картинку
        if (file != null && !file.isEmpty()) {
            String fileName = fileService.uploadFile(file);
            String publicImageUrl = minioPublicUrl + "/shyn-images/" + fileName;
            route.setImageUrl(publicImageUrl);
        }

        return routeRepository.save(route);
    }

    // 5. Удаление маршрута
    public void deleteRoute(Long id) {
        Route route = getRouteById(id);

        // Удаляем файл из MinIO перед удалением записи из БД
        if (route.getImageUrl() != null) {
            fileService.deleteFile(route.getImageUrl());
        }

        routeRepository.delete(route);
    }

}