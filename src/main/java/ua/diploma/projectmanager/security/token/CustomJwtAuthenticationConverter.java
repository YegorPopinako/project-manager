package ua.diploma.projectmanager.security.token;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtException;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.stereotype.Component;
import ua.diploma.projectmanager.security.service.TokenService;

import java.util.Collection;

@Component
@RequiredArgsConstructor
public class CustomJwtAuthenticationConverter {
    private final TokenService tokenService;
    private final JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter;

    public AbstractAuthenticationToken convert(Jwt jwt) {
        if (tokenService.isTokenBlacklisted(jwt.getTokenValue())) {
            throw new JwtException("Token is revoked or expired");
        }
        Collection<GrantedAuthority> authorities = grantedAuthoritiesConverter.convert(jwt);
        return new JwtAuthenticationToken(jwt, authorities);
    }
}
