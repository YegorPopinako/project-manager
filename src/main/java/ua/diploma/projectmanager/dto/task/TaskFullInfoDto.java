package ua.diploma.projectmanager.dto.task;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.diploma.projectmanager.model.Project;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.security.enums.TaskPriority;
import ua.diploma.projectmanager.security.enums.TaskStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskFullInfoDto {

    private Long id;

    private String title;

    private String description;

    private TaskStatus status;

    private TaskPriority priority;

    private LocalDate endPoint;

    private LocalDateTime updatedAt;

    private User user;

    private Project project;
}
