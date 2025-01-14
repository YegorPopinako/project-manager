package ua.diploma.projectmanager.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.dto.project.ProjectDto;
import ua.diploma.projectmanager.dto.project.ProjectFullInfoDto;
import ua.diploma.projectmanager.dto.project.ProjectUpdateDto;
import ua.diploma.projectmanager.model.Project;
import ua.diploma.projectmanager.repository.ProjectRepository;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;

    public ProjectFullInfoDto getProject(Long id) {
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Project with id " + id + " not found"));
        return modelMapper.map(project, ProjectFullInfoDto.class);
    }

    @Transactional
    public ProjectFullInfoDto createProject(ProjectDto projectDto) {
        Project project = modelMapper.map(projectDto, Project.class);
        return modelMapper.map(projectRepository.save(project), ProjectFullInfoDto.class);
    }

    @Transactional
    public ProjectFullInfoDto updateProject(ProjectUpdateDto projectDto) {
        if (isNull(projectDto.getId()) || !projectRepository.existsById(projectDto.getId())) {
            throw new EntityNotFoundException("Invalid ID value");
        }
        Project entity = modelMapper.map(projectDto, Project.class);

        return modelMapper.map(projectRepository.save(entity), ProjectFullInfoDto.class);
    }

    @Transactional
    public void deleteProject(Long id) {
        getProject(id);
        projectRepository.deleteById(id);
    }
}
