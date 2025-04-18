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
import ua.diploma.projectmanager.model.UserProject;
import ua.diploma.projectmanager.model.UserProjectId;
import ua.diploma.projectmanager.repository.ProjectRepository;
import ua.diploma.projectmanager.repository.UserProjectRepository;
import ua.diploma.projectmanager.repository.UserRepository;
import ua.diploma.projectmanager.security.enums.Role;
import ua.diploma.projectmanager.service.mapper.ProjectMapper;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final ProjectMapper projectMapper;
    private final UserRepository userRepository;
    private final UserProjectRepository userProjectRepository;

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

        project = projectRepository.save(project);

        UserProject userProject = new UserProject();
        userProject.setUser(creator);
        userProject.setProject(project);
        userProject.setRole(Role.ADMIN);
        userProject.setId(new UserProjectId(creator.getId(), project.getId()));

        userProjectRepository.save(userProject);

        return modelMapper.map(project, ProjectFullInfoDto.class);
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
        userProjectRepository.deleteProjectById(id);
        projectRepository.deleteById(id);
    }
}
