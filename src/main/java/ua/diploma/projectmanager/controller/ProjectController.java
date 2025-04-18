package ua.diploma.projectmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import ua.diploma.projectmanager.security.enums.AssignmentType;
import ua.diploma.projectmanager.service.ProjectService;
import ua.diploma.projectmanager.service.UserService;

@RestController
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;

    @Operation(summary = "Get project by ID")
    @ApiResponse(responseCode = "200", description = "Project found")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @GetMapping("/{id}")
    @PreAuthorize("@userService.isUserAssignedToProject(#id, authentication.name)")
    public ProjectFullInfoDto getProject(@PathVariable Long id) {
        return projectService.getProject(id);
    }

    @Operation(summary = "Create new project")
    @ApiResponse(responseCode = "201", description = "Project created")
    @ApiResponse(responseCode = "400", description = "Invalid project data")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectFullInfoDto createProject(@Valid @RequestBody ProjectDto projectDto,
                                            Authentication authentication) {
        return projectService.createProject(projectDto, authentication.getName());
    }

    @Operation(summary = "Update existing project")
    @ApiResponse(responseCode = "200", description = "Project updated")
    @ApiResponse(responseCode = "400", description = "Invalid project data")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @PutMapping
    @PreAuthorize("@userService.isUserAdminOfProject(#projectUpdateDto.id, authentication.name)")
    public ProjectFullInfoDto updateProject(@Valid @RequestBody ProjectUpdateDto projectUpdateDto) {
        return projectService.updateProject(projectUpdateDto);
    }

    @Operation(summary = "Delete project by ID")
    @ApiResponse(responseCode = "200", description = "Project deleted")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.isUserAdminOfProject(#id, authentication.name)")
    public void deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
    }

    @Operation(summary = "Assign user to project")
    @ApiResponse(responseCode = "200", description = "User assigned to project")
    @ApiResponse(responseCode = "404", description = "User or project not found")
    @PostMapping("/assign")
    @PreAuthorize("@userService.isUserAdminOfProject(#assignUserDto.id, authentication.name)")
    public ResponseEntity<String> assignUserToProject(@RequestBody AssignUserDto assignUserDto) {
        userService.assignUser(new AssignUserDto(
                assignUserDto.getId(),
                assignUserDto.getUserEmail(),
                AssignmentType.PROJECT));

        return ResponseEntity.ok("User %s assigned to project %d successfully".formatted(
                assignUserDto.getUserEmail(), assignUserDto.getId()
        ));
    }

    @Operation(summary = "Unassign user from project")
    @ApiResponse(responseCode = "200", description = "User unassigned from project")
    @ApiResponse(responseCode = "404", description = "User or project not found")
    @PostMapping("/unassign")
    @PreAuthorize("@userService.isUserAdminOfProject(#assignUserDto.id, authentication.name)")
    public ResponseEntity<String> unassignUserFromProject(@RequestBody AssignUserDto assignUserDto) {
        userService.unassignUser(new AssignUserDto(
                assignUserDto.getId(),
                assignUserDto.getUserEmail(),
                AssignmentType.PROJECT));

        return ResponseEntity.ok("User %s unassigned to project %d successfully".formatted(
                assignUserDto.getUserEmail(), assignUserDto.getId()
        ));
    }
}
