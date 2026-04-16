package pro.routes.dto;

import lombok.*;
import pro.routes.model.TrackComment;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {

    private Long id;
    private Long userId;
    private String username;
    private String text;
    private LocalDateTime createdAt;

    public static CommentResponse from(TrackComment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .userId(comment.getUserId())
                .username(comment.getUsername())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}
