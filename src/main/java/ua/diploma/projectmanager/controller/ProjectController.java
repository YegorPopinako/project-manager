package ua.diploma.projectmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.project.ProjectDto;
import ua.diploma.projectmanager.dto.project.ProjectFullInfoDto;
import ua.diploma.projectmanager.dto.project.ProjectUpdateDto;
import ua.diploma.projectmanager.service.ProjectService;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'DEV', 'MANAGER', 'ADMIN')")
    public ProjectFullInfoDto getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ProjectFullInfoDto createProject(@Valid @RequestBody ProjectDto projectDto) {
        return projectService.createProject(projectDto);
    }

    @PutMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ProjectFullInfoDto updateProject(@Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(projectUpdateDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }
}
