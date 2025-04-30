package ua.diploma.projectmanager.dto.project;

import lombok.Data;
import ua.diploma.projectmanager.dto.task.TaskFullInfoDto;

import java.util.List;

@Data
public class ProjectFullInfoDto {

    private Long id;

    private String title;

    private String description;

    private List<TaskFullInfoDto> tasks;
}
