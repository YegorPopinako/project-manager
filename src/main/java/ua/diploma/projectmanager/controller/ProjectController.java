package ua.diploma.projectmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.project.ProjectDto;
import ua.diploma.projectmanager.dto.project.UpdateProjectDto;
import ua.diploma.projectmanager.model.Project;
import ua.diploma.projectmanager.service.ProjectService;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    public Project getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Project createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping
    public Project updateProject(@Valid @RequestBody UpdateProjectDto updateProjectDto) {
        return projectService.updateProject(updateProjectDto);
    }

    @DeleteMapping("/{id}")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
