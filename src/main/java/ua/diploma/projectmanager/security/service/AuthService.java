package ua.diploma.projectmanager.security.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.dto.user.SignInDto;
import ua.diploma.projectmanager.dto.user.SignInResponseDto;
import ua.diploma.projectmanager.dto.user.SignUpDto;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.exception.EmailAlreadyInUseException;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final JWTService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public UserFullInfoDto signUp(SignUpDto userDto) {
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new EmailAlreadyInUseException("Email %s is already in use".formatted(userDto.getEmail()));
        }
        User user = modelMapper.map(userDto, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return modelMapper.map(userRepository.save(user), UserFullInfoDto.class);
    }

    public SignInResponseDto signIn(SignInDto signInDto) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                signInDto.getEmail(), signInDto.getPassword()
        ));
        return new SignInResponseDto(jwtService.generateToken(userRepository.findByEmail(signInDto.getEmail()).get()));
    }
}
