package ua.diploma.projectmanager.security.enums;

import org.springframework.security.core.GrantedAuthority;

public enum Role implements GrantedAuthority {
    USER,
    DEV,
    ADMIN,
    MANAGER;

    @Override
    public String getAuthority() {
        return "ROLE_" + name();
    }
}
