package ua.diploma.projectmanager.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.dto.user.AssignUserDto;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.dto.user.UserUpdateDto;
import ua.diploma.projectmanager.exception.EmailAlreadyInUseException;
import ua.diploma.projectmanager.model.Project;
import ua.diploma.projectmanager.model.Task;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.model.UserProject;
import ua.diploma.projectmanager.repository.ProjectRepository;
import ua.diploma.projectmanager.repository.TaskRepository;
import ua.diploma.projectmanager.repository.UserProjectRepository;
import ua.diploma.projectmanager.repository.UserRepository;
import ua.diploma.projectmanager.security.enums.AssignmentType;
import ua.diploma.projectmanager.security.enums.Role;
import ua.diploma.projectmanager.service.mapper.UserMapper;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;
    private final UserMapper userMapper;
    private final UserProjectRepository userProjectRepository;

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

    @Transactional
    public void assignUser(AssignUserDto dto) {
        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Object entity = getEntityByType(dto.getType(), dto.getId());

        if (entity instanceof Project project) {
            if (!userProjectRepository.existsByUserIdAndProjectId(user.getId(), project.getId())) {
                UserProject userProject = new UserProject();
                userProject.setUser(user);
                userProject.setProject(project);
                userProject.setRole(Role.USER);
                userProjectRepository.save(userProject);
            }
        } else if (entity instanceof Task task) {
            validateUserProjectAssignment(task.getId(), user.getEmail());
            task.setUser(user);
            taskRepository.save(task);
        }
    }

    @Transactional
    public void unassignUser(AssignUserDto dto) {
        User user = userRepository.findByEmail(dto.getUserEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Object entity = getEntityByType(dto.getType(), dto.getId());

        if (entity instanceof Project project) {
            userProjectRepository.deleteByUserIdAndProjectId(user.getId(), project.getId());
        } else if (entity instanceof Task task) {
            validateTaskUnassignment(task, user);
            task.setUser(null);
            taskRepository.save(task);
        }
    }

    @Transactional
    public void registerIfAbsent(String email, String firstName, String lastName, String profileImage) {
        if(!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setProfileImage(profileImage);
            userRepository.save(user);
        }
    }

    public boolean isUserAdminOfProject(Long projectId, String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getId();

        return userProjectRepository.existsByUserIdAndProjectIdAndRole(userId, projectId, Role.ADMIN);
    }

    public boolean isUserAssignedToProject(Long projectId, String email) {
        Long userId = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"))
                .getId();

        return userProjectRepository.existsByUserIdAndProjectId(userId, projectId);
    }

    public boolean isUserAssignedToProjectByTask(Long taskId, String email) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        Long projectId = task.getProject().getId();
        return isUserAssignedToProject(projectId, email);
    }

    private Object getEntityByType(AssignmentType type, Long id) {
        return switch (type) {
            case PROJECT -> projectRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Project not found"));
            case TASK -> taskRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Task not found"));
        };
    }

    private void validateTaskUnassignment(Task task, User user) {
        Optional.ofNullable(task.getUser())
                .orElseThrow(() -> new IllegalStateException("Task is not assigned to any user"));

        if (!task.getUser().equals(user)) {
            throw new IllegalStateException("Task is assigned to a different user");
        }
    }

    private void validateUserProjectAssignment(Long id, String email) {
        if (!isUserAssignedToProjectByTask(id, email)) {
            throw new IllegalStateException("User is not assigned to project related to this task");
        }
    }
}
