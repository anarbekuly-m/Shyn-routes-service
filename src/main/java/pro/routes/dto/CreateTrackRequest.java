package pro.routes.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateTrackRequest {

    private String name;
    private String activityType;   // "WALKING", "RUNNING", "HIKING", "CYCLING"
    private Double distanceKm;
    private Long durationSec;
    private String gpxUrl;         // URL от POST /upload/gpx (или null)
    private List<String> imageUrls; // URL-ы от POST /upload/photos (или пустой)
}
