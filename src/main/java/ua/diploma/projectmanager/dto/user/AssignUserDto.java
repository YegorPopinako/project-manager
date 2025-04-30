package ua.diploma.projectmanager.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.diploma.projectmanager.security.enums.AssignmentType;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignUserDto {

    private Long id;
    private String userEmail;
    private AssignmentType type;
}
