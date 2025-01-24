package ua.diploma.projectmanager.security.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.user.SignInDto;
import ua.diploma.projectmanager.dto.user.SignInResponseDto;
import ua.diploma.projectmanager.dto.user.SignUpDto;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.security.service.AuthService;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public UserFullInfoDto signUp(@RequestBody @Validated SignUpDto dto) {
        return authService.signUp(dto);
    }

    @PostMapping("/sign-in")
    public SignInResponseDto signIn(@RequestBody SignInDto signInDto) {
        return authService.signIn(signInDto);
    }
}
