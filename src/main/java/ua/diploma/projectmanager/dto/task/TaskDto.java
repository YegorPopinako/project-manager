package ua.diploma.projectmanager.dto.task;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskDto {

    @NotNull(message = "Title must not be empty")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotNull(message = "Description must not be empty")
    @Size(min = 3, max = 255, message = "Description must be between 5 and 255 characters")
    private String description;

    @NotBlank(message = "Status is required")
    private String status;

    @NotNull(message = "Project ID is required")
    private Long projectId;

    @NotNull(message = "Estimate is required")
    private Integer estimate;
}

