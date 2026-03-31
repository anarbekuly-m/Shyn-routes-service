package pro.routes.routing.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "routes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;        // Например: "Пик Фурманова"

    private String location;    // "Заилийский Алатау"

    private String difficulty;  // "Medium"

    private Double distance;    // 14.5

    private String category;    // "peak"

    private Double latitude;    // 43.1533

    private Double longitude;   // 77.0867

    @Column(name = "image_url")
    private String imageUrl;    // Ссылка на фото в MinIO
}