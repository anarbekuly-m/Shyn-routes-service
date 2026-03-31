package pro.routes.routing.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Отключаем CSRF, чтобы работали POST/PUT/DELETE запросы из Postman
                .csrf(csrf -> csrf.disable())

                // 2. Разрешаем всем доступ ко всем эндпоинтам (пока в разработке)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // 3. Отключаем стандартную форму логина (ту самую, что ты видишь)
                .formLogin(form -> form.disable())

                // 4. Отключаем Basic Auth (всплывающее окно браузера)
                .httpBasic(basic -> basic.disable());

        return http.build();
    }
}