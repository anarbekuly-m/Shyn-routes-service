package pro.routes.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import pro.routes.model.Route;
import pro.routes.model.RouteImage;
import pro.routes.repository.RouteRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "routes")
public class RouteService {

    private final RouteRepository routeRepository;
    private final FileService fileService;

    @Value("${MINIO_PUBLIC_URL:http://localhost:9000}")
    private String minioPublicUrl;

    @Cacheable(key = "'all'")
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    @Cacheable(key = "#id")
    public Route getRouteById(Long id) {
        // Теперь просто findById.
        // Благодаря FetchType.EAGER в модели, Hibernate сам подтянет картинки за один присест.
        return routeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Маршрут не найден"));
    }

    @Transactional
    @CacheEvict(key = "'all'", allEntries = true)
    public Route createRoute(Route route, List<MultipartFile> files) throws Exception {
        // Сначала сохраняем базу маршрута
        Route savedRoute = routeRepository.save(route);

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String fullPath = "route-" + savedRoute.getId() + "/" + fileName;

                fileService.uploadFile(file, fullPath);

                String finalUrl = minioPublicUrl + "/shyn-images/" + fullPath;
                RouteImage img = new RouteImage();
                img.setImageUrl(finalUrl);
                img.setRoute(savedRoute);
                savedRoute.getImages().add(img);
            }
            // Пересохраняем уже с картинками
            return routeRepository.save(savedRoute);
        }
        return savedRoute;
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'all'", allEntries = true), // Рекомендую добавить allEntries для надежности списка
            @CacheEvict(key = "#id")
    })
    public Route updateRoute(Long id, Route routeDetails, List<MultipartFile> newFiles) throws Exception {
        Route route = getRouteById(id);

        route.setName(routeDetails.getName());
        route.setLocation(routeDetails.getLocation());
        route.setDescription(routeDetails.getDescription());
        route.setElevation(routeDetails.getElevation());
        route.setDifficulty(routeDetails.getDifficulty());
        route.setCategory(routeDetails.getCategory());
        route.setLatitude(routeDetails.getLatitude());
        route.setLongitude(routeDetails.getLongitude());

        if (newFiles != null && !newFiles.isEmpty()) {
            for (MultipartFile file : newFiles) {
                String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                String fullPath = "route-" + route.getId() + "/" + fileName;
                fileService.uploadFile(file, fullPath);

                String finalUrl = minioPublicUrl + "/shyn-images/" + fullPath;
                RouteImage img = new RouteImage();
                img.setImageUrl(finalUrl);
                img.setRoute(route);
                route.getImages().add(img);
            }
        }
        return routeRepository.save(route);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'all'", allEntries = true),
            @CacheEvict(key = "#id")
    })
    public void deleteRoute(Long id) {
        Route route = getRouteById(id);
        if (route.getImages() != null) {
            for (RouteImage img : route.getImages()) {
                fileService.deleteFile(img.getImageUrl());
            }
        }
        routeRepository.delete(route);
    }
}