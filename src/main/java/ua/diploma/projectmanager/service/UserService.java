package ua.diploma.projectmanager.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.dto.user.UserUpdateDto;
import ua.diploma.projectmanager.exception.EmailAlreadyInUseException;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.repository.UserRepository;
import ua.diploma.projectmanager.service.mapper.UserMapper;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;

    public UserFullInfoDto getUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " not found"));
        return modelMapper.map(user, UserFullInfoDto.class);
    }

    @Transactional
    public UserFullInfoDto updateUser(UserUpdateDto userDto) {
        var user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with id: %s not found".formatted(userDto.getId())));

        if (userRepository.existsByEmailAndIdNot(userDto.getEmail(), userDto.getId())) {
            throw new EmailAlreadyInUseException("Email already in use");
        }

        userMapper.mapUserFromUserDto(userDto, user);

        return modelMapper.map(userRepository.save(user), UserFullInfoDto.class);
    }

    @Transactional
    public void deleteUser(Long id) {
        getUser(id);
        userRepository.deleteById(id);
    }
}
