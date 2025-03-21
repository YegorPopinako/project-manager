package ua.diploma.projectmanager.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.user.SignUpDto;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.security.service.AuthService;

import java.time.Instant;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoUnit.MINUTES;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtEncoder jwtEncoder;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserFullInfoDto signUp(@RequestBody @Validated SignUpDto dto) {
        return authService.signUp(dto);
    }

    @PostMapping("/login")
    public String login(@AuthenticationPrincipal User user) {
        var now = Instant.now();
        var claims = JwtClaimsSet.builder()
                .issuer("self")
                .issuedAt(now)
                .expiresAt(now.plus(5, MINUTES))
                .subject(user.getUsername())
                .claim("authorities", createAuthorities(user))
                .build();
        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    private String createAuthorities(User user) {
        return user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
    }
}
