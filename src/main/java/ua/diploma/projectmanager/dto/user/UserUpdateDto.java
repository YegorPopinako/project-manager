package ua.diploma.projectmanager.dto.user;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UserUpdateDto extends UserDto {

    @Min(value = 1, message = "Id must be greater than 0")
    @NotNull
    private Long id;

    private Set<String> roles;
}
