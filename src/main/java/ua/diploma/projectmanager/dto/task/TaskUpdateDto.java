package ua.diploma.projectmanager.dto.task;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskUpdateDto extends TaskDto {

    @Min(value = 1, message = "Id must be greater than 0")
    @NotNull
    private Long id;
}
