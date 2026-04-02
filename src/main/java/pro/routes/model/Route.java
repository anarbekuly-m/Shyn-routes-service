package pro.routes.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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
    private String name;
    private String location;

    @Column(columnDefinition = "TEXT")
    private String description;

    private Double elevation;   // Высота
    private String difficulty;  // Сложность
    private String category;    // Категория
    private Double latitude;
    private Double longitude;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<RouteImage> images = new ArrayList<>();
}