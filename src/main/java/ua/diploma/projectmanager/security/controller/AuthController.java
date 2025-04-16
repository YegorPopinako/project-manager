package ua.diploma.projectmanager.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.diploma.projectmanager.dto.user.SignUpDto;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.security.service.AuthService;
import ua.diploma.projectmanager.security.service.TokenService;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final TokenService tokenService;
    private final AuthService authService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public UserFullInfoDto signUp(@RequestBody @Validated SignUpDto dto) {
        return authService.signUp(dto);
    }

    @PostMapping("/login")
    public String login(@AuthenticationPrincipal User user) {
       return tokenService.generateToken(user);
    }

    @PostMapping("/ui/logout")
    public void logout(Authentication authentication) {
        tokenService.revokeToken(authentication.getName());
    }
}
