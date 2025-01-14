package ua.diploma.projectmanager.dto.project;

import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectUpdateDto extends ProjectDto {

    @Min(value = 1, message = "Id must be greater than 0")
    private Long id;
}
