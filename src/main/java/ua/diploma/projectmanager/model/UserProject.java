package ua.diploma.projectmanager.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ua.diploma.projectmanager.security.enums.Role;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_project")
public class UserProject {

    @EmbeddedId
    private UserProjectId id = new UserProjectId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("projectId")
    private Project project;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
}

