package pro.routes.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import java.time.Duration;

@Configuration
@EnableCaching
public class RedisConfig {

    // Используем те же имена, что в docker-compose
    @Value("${SPRING_DATA_REDIS_HOST:localhost}")
    private String redisHost;

    @Value("${SPRING_DATA_REDIS_PORT:6379}")
    private int redisPort;

    @Value("${SPRING_DATA_REDIS_PASSWORD:}") // Пароль может быть пустым
    private String password;

    @Bean
    public JedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(redisHost, redisPort);
        if (password != null && !password.isEmpty()) {
            config.setPassword(RedisPassword.of(password));
        }
        return new JedisConnectionFactory(config);
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(RedisCacheConfiguration.defaultCacheConfig()
                        .entryTtl(Duration.ofHours(1)))
                .build();
    }
}