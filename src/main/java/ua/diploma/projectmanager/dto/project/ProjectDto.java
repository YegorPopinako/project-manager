package ua.diploma.projectmanager.dto.project;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProjectDto {

    @NotNull(message = "Title must not be empty")
    @Size(min = 3, max = 255, message = "Title must be between 3 and 255 characters")
    private String title;

    @NotNull(message = "Description must not be empty")
    @Size(min = 3, max = 255, message = "Description must be between 5 and 255 characters")
    private String description;
}
