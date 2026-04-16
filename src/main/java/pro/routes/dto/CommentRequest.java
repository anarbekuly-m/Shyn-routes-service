package pro.routes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CommentRequest {

    @NotBlank(message = "Comment text is required")
    @Size(max = 500, message = "Comment must be less than 500 characters")
    private String text;
}
