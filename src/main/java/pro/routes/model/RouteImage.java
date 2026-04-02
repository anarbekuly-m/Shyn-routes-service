package pro.routes.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RouteImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "route_id")
    @com.fasterxml.jackson.annotation.JsonIgnore // Чтобы не было зацикливания в JSON
    private Route route;
}