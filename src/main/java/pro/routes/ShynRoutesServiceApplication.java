package pro.routes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class ShynRoutesServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShynRoutesServiceApplication.class, args);
    }

}
