package ua.diploma.projectmanager.dto.task;

import lombok.Data;

@Data
public class UpdateTaskStatusDto {
    private Long taskId;
    private String newStatus;
}
