package ua.diploma.projectmanager.dto.task;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.diploma.projectmanager.security.enums.TaskStatus;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class TaskDto {

    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @Size(min = 3, max = 255, message = "Description must be between 5 and 255 characters")
    private String description;

    private TaskStatus status;

    private LocalDate endPoint;

    private Long projectId;
}
