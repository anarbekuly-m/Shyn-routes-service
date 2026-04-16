package pro.routes.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserStatsResponse {
    private Long userId;
    private String username;
    private Integer totalActivities;
    private Double totalDistanceKm;
    private Long totalDurationSec;
}
