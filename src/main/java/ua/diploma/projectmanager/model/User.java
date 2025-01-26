package ua.diploma.projectmanager.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import ua.diploma.projectmanager.security.login.Role;

import java.util.Collection;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@ToString(exclude = "roles")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    private String displayName;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email", unique = true)
    private String email;

    private String password;

    private int age;

    @CollectionTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "role"}, name = "uk_user_role")
    )
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @PostPersist
    @PostUpdate
    private void calculateDisplayName() {
        this.displayName = firstName + " " + lastName;
    }
}
