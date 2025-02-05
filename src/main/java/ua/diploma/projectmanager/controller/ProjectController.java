package ua.diploma.projectmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.project.ProjectDto;
import ua.diploma.projectmanager.dto.project.ProjectFullInfoDto;
import ua.diploma.projectmanager.dto.project.ProjectUpdateDto;
import ua.diploma.projectmanager.dto.user.AssignUserDto;
import ua.diploma.projectmanager.service.ProjectService;
import ua.diploma.projectmanager.service.UserService;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isUserAssignedToProject(#id, authentication.principal.username)")
    public ProjectFullInfoDto getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ProjectFullInfoDto createProject(@Valid @RequestBody ProjectDto projectDto,
                                            Authentication authentication) {
        return projectService.createProject(projectDto, authentication.getName());
    }

    @PutMapping
    @PreAuthorize("(hasRole('ADMIN')) or (hasRole('MANAGER') and @userService.isUserAssignedToProject(#projectUpdateDto.id, authentication.principal.username))")
    public ProjectFullInfoDto updateProject(@Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(projectUpdateDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("(hasRole('ADMIN')) or (hasRole('MANAGER') and @userService.isUserAssignedToProject(#id, authentication.principal.username))")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @PostMapping("/assign")
    @PreAuthorize("(hasRole('ADMIN')) or (hasRole('MANAGER') and @userService.isUserAssignedToProject(#assignUserDto.id, authentication.principal.username))")
    public ResponseEntity<String> assignUserToProject(@RequestBody AssignUserDto assignUserDto) {
        userService.assignUser(assignUserDto);
        return ResponseEntity.ok("User %s assigned to project %d successfully".formatted(
                assignUserDto.getUserEmail(), assignUserDto.getId()
        ));
    }

    @PostMapping("/unassign")
    @PreAuthorize("(hasRole('ADMIN')) or (hasRole('MANAGER') and @userService.isUserAssignedToProject(#assignUserDto.id, authentication.principal.username))")
    public ResponseEntity<String> unassignUserFromProject(@RequestBody AssignUserDto assignUserDto) {
        userService.unassignUser(assignUserDto);
        return ResponseEntity.ok("User %s unassigned to project %d successfully".formatted(
                assignUserDto.getUserEmail(), assignUserDto.getId()
        ));
    }
}
