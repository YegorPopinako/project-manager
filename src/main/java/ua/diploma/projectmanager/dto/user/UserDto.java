package ua.diploma.projectmanager.dto.user;

import jakarta.validation.constraints.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class UserDto {

    @Size(min = 2, max = 50, message = "Invalid first name size [cannot be less than 2 and bigger than 50]")
    @Pattern(regexp = "^[^0-9]*$", message = "First name should not contain numbers")
    private String firstName;

    @Size(min = 2, max = 50, message = "Invalid last name size [cannot be less than 2 and bigger than 50]")
    @Pattern(regexp = "^[^0-9]*$", message = "Last name should not contain numbers")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotNull
    private String email;

    @Min(value = 0, message = "invalid age value")
    @Max(value = 200, message = "Age cannot be more than 200")
    private int age;
}
