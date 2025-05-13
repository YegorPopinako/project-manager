package ua.diploma.projectmanager.dto.user;

import lombok.Data;

@Data
public class UserFullInfoDto {

    private Long id;
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
}
