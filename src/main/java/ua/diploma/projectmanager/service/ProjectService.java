package ua.diploma.projectmanager.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.dto.project.ProjectDto;
import ua.diploma.projectmanager.dto.project.ProjectFullInfoDto;
import ua.diploma.projectmanager.dto.project.ProjectUpdateDto;
import ua.diploma.projectmanager.model.Project;
import ua.diploma.projectmanager.model.User;
import ua.diploma.projectmanager.repository.ProjectRepository;
import ua.diploma.projectmanager.repository.UserRepository;
import ua.diploma.projectmanager.security.enums.Role;
import ua.diploma.projectmanager.service.mapper.ProjectMapper;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;

    public ProjectFullInfoDto getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + id + " not found"));
        return modelMapper.map(project, ProjectFullInfoDto.class);
    }

    @Transactional
    public ProjectFullInfoDto createProject(ProjectDto projectDto, String email) {
        Project project = modelMapper.map(projectDto, Project.class);

        User creator = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (!creator.getRoles().contains(Role.ADMIN)) {
            project.setUsers(Set.of(creator));
        }

        return modelMapper.map(projectRepository.save(project), ProjectFullInfoDto.class);
    }

    @Transactional
    public ProjectFullInfoDto updateProject(ProjectUpdateDto projectDto) {
        var project = projectRepository.findById(projectDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("User with id: %s not found".formatted(projectDto.getId())));

        projectMapper.mapProjectFromProjectDto(projectDto, project);

        return modelMapper.map(projectRepository.save(project), ProjectFullInfoDto.class);
    }

    @Transactional
    public void deleteProject(Long id) {
        getProject(id);
        projectRepository.deleteById(id);
    }
}
