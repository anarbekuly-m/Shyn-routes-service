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

    // ...
    @Transactional
    @CacheEvict(key = "'all'", allEntries = true)
    public Route createRoute(Route route, List<MultipartFile> files, MultipartFile gpxFile) throws Exception {
        // 1. Сохраняем базу, чтобы получить ID
        Route savedRoute = routeRepository.save(route);

        // 2. Обработка GPX файла
        if (gpxFile != null && !gpxFile.isEmpty()) {
            String gpxFileName = "track_" + UUID.randomUUID() + ".gpx";
            String gpxPath = "route-" + savedRoute.getId() + "/" + gpxFileName;

            fileService.uploadFile(gpxFile, gpxPath);
            savedRoute.setGpxUrl(minioPublicUrl + "/shyn-images/" + gpxPath);
        }

        // 3. Обработка изображений (твой текущий код)
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
        }
        return routeRepository.save(savedRoute);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(key = "'all'", allEntries = true),
            @CacheEvict(key = "#id")
    })
    public Route updateRoute(Long id, Route routeDetails, List<MultipartFile> newFiles, MultipartFile gpxFile) throws Exception {
        Route route = getRouteById(id);

        // Обновление текстовых полей
        route.setName(routeDetails.getName());
        route.setLocation(routeDetails.getLocation());
        route.setDescription(routeDetails.getDescription());
        route.setElevation(routeDetails.getElevation());
        route.setDifficulty(routeDetails.getDifficulty());
        route.setCategory(routeDetails.getCategory());
        route.setLatitude(routeDetails.getLatitude());
        route.setLongitude(routeDetails.getLongitude());

        // Обновление GPX (если прислали новый)
        if (gpxFile != null && !gpxFile.isEmpty()) {
            // Удаляем старый файл, если он был
            if (route.getGpxUrl() != null) {
                fileService.deleteFile(route.getGpxUrl());
            }

            String gpxFileName = "track_" + UUID.randomUUID() + ".gpx";
            String gpxPath = "route-" + route.getId() + "/" + gpxFileName;
            fileService.uploadFile(gpxFile, gpxPath);
            route.setGpxUrl(minioPublicUrl + "/shyn-images/" + gpxPath);
        }

        // Обработка новых фото
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

        // Удаляем GPX из MinIO
        if (route.getGpxUrl() != null) {
            fileService.deleteFile(route.getGpxUrl());
        }

        // Удаляем картинки
        if (route.getImages() != null) {
            for (RouteImage img : route.getImages()) {
                fileService.deleteFile(img.getImageUrl());
            }
        }
        routeRepository.delete(route);
    }
}