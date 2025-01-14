package ua.diploma.projectmanager.dto.task;

import lombok.Data;
import lombok.NoArgsConstructor;
import ua.diploma.projectmanager.dto.project.ProjectFullInfoDto;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class TaskFullInfoDto {

    private Long id;

    private String title;

    private String description;

    private String status;

    private Integer estimate;

    private LocalDateTime startPoint;

    private LocalDateTime endPoint;

    private LocalDateTime updatedAt;

    private ProjectFullInfoDto project;
}

