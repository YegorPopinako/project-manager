package ua.diploma.projectmanager.dto.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import ua.diploma.projectmanager.security.enums.AssignmentType;

@Data
@AllArgsConstructor
public class AssignUserDto {

    private Long id;
    private String userEmail;
    private AssignmentType type;
}
