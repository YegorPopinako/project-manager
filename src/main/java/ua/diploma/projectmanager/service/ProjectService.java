package ua.diploma.projectmanager.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.dto.project.ProjectDto;
import ua.diploma.projectmanager.dto.project.UpdateProjectDto;
import ua.diploma.projectmanager.model.Project;
import ua.diploma.projectmanager.repository.ProjectRepository;

import java.util.Optional;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public Project getProject(Long id) {
        if(isNull(id) || !projectRepository.existsById(id)){
            throw new EntityNotFoundException("Invalid id value");
        }
        Optional<Project> project = projectRepository.findById(id);
        return project.orElseThrow(() -> new EntityNotFoundException("Project with id " + id + " not found"));
    }

    @Transactional
    public Project createProject(ProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);
        return projectRepository.save(project);
    }

    @Transactional
    public Project updateProject(UpdateProjectDto projectDto) {
        Project existingProject = getProject(projectDto.getId());
        existingProject.setTitle(projectDto.getTitle());
        existingProject.setDescription(projectDto.getDescription());
        return projectRepository.save(existingProject);
    }

    @Transactional
    public void deleteProject(Long id) {
        getProject(id);
        projectRepository.deleteById(id);
    }
}
