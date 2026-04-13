package pro.routes.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Добавлен импорт для логирования
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile; // Добавлен импорт
import pro.routes.model.UserTrack;
import pro.routes.repository.UserTrackRepository;

import java.util.List;
import java.util.UUID; // Добавлен импорт для генерации имен файлов

@Service
@RequiredArgsConstructor
@Slf4j // Аннотация для работы log.info
public class UserTrackService {

    private final UserTrackRepository repository;
    private final ObjectMapper objectMapper;
    private final FileService fileService; // ИСПРАВЛЕНО: теперь соответствует названию твоего класса

    @CacheEvict(value = "userTracks", key = "#userId")
    public UserTrack saveTrack(String trackJson, MultipartFile file, Long userId) throws Exception {

        // 1. Парсим JSON в объект (название трека, дистанция и т.д.)
        UserTrack track = objectMapper.readValue(trackJson, UserTrack.class);

        // 2. Устанавливаем ID пользователя, который мы достали из JWT
        track.setUserId(userId);

        // 3. Загружаем GPX-файл в MinIO и получаем URL
        if (file != null && !file.isEmpty()) {
            // Формируем путь: tracks/ID_ЮЗЕРА/random_uuid.gpx
            String fileName = "tracks/" + userId + "/" + UUID.randomUUID() + ".gpx";

            // Вызываем твой FileService (убедись, что он теперь возвращает String URL)
            String url = fileService.uploadFile(file, fileName);
            track.setGpxUrl(url);
        }

        // 4. Сохраняем все в PostgreSQL
        log.info("Saving new track for user {}: {}", userId, track.getName());
        return repository.save(track);
    }

    @Cacheable(value = "userTracks", key = "#userId")
    public List<UserTrack> getTracksByUserId(Long userId) {
        return repository.findByUserId(userId);
    }
}