package ua.diploma.projectmanager.dto.user;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class UserUpdateDto extends UserDto {

    private Long id;

    private Set<String> roles;
}
