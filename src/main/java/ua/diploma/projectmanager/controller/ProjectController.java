package ua.diploma.projectmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ua.diploma.projectmanager.dto.project.ProjectDto;
import ua.diploma.projectmanager.dto.project.ProjectFullInfoDto;
import ua.diploma.projectmanager.dto.project.ProjectUpdateDto;
import ua.diploma.projectmanager.dto.user.AssignUserDto;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.model.Notification;
import ua.diploma.projectmanager.repository.UserProjectRepository;
import ua.diploma.projectmanager.service.NotificationService;
import ua.diploma.projectmanager.service.ProjectService;
import ua.diploma.projectmanager.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/api/project")
@RequiredArgsConstructor
public class ProjectController {

    private final ProjectService projectService;
    private final UserService userService;
    private final UserProjectRepository userProjectRepository;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    @Operation(summary = "Get project by ID")
    @ApiResponse(responseCode = "200", description = "Project found")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @GetMapping("/{id}")
    @PreAuthorize("@userService.isUserAssignedToProject(#id, authentication.name)")
    public String getProject(@PathVariable Long id, Model model, Authentication authentication) {
        ProjectFullInfoDto fullInfoDto = projectService.getProject(id);
        boolean isOwner = userService.isUserAdminOfProject(id, authentication.getName());
        List<UserFullInfoDto> users = userProjectRepository.getUsersByProjectId(id)
                .stream()
                .map(user -> modelMapper.map(user, UserFullInfoDto.class))
                .toList();
        ProjectUpdateDto updateDto = new ProjectUpdateDto();
        updateDto.setId(id);
        List<Notification> notifications = notificationService.getUserNotifications(authentication.getName());
        model.addAttribute("notifications", notifications);
        model.addAttribute("projectDto", fullInfoDto);
        model.addAttribute("isOwner", isOwner);
        model.addAttribute("projectUpdateDto", updateDto);
        model.addAttribute("users", users);
        model.addAttribute("assignUserDto", new AssignUserDto());
        model.addAttribute("authentication", authentication);
        return "project";
    }

    @PostMapping()
    public String createProject(@ModelAttribute ProjectDto projectDto,
                                Authentication authentication,
                                RedirectAttributes redirectAttributes) {
        projectService.createProject(projectDto, authentication.getName());
        redirectAttributes.addFlashAttribute("message", "Project created successfully!");
        return "redirect:/home";
    }

    @Operation(summary = "Update existing project")
    @ApiResponse(responseCode = "200", description = "Project updated")
    @ApiResponse(responseCode = "400", description = "Invalid project data")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @PutMapping
    @PreAuthorize("@userService.isUserAdminOfProject(#projectUpdateDto.id, authentication.name)")
    public String updateProject(@ModelAttribute ProjectUpdateDto projectUpdateDto) {
        projectService.updateProject(projectUpdateDto);
        return "redirect:/api/project/" + projectUpdateDto.getId();
    }

    @Operation(summary = "Delete project by ID")
    @ApiResponse(responseCode = "200", description = "Project deleted")
    @ApiResponse(responseCode = "404", description = "Project not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.isUserAdminOfProject(#id, authentication.name)")
    public String deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return "redirect:/home";
    }

    @Operation(summary = "Unassign user from project")
    @ApiResponse(responseCode = "200", description = "User unassigned from project")
    @ApiResponse(responseCode = "404", description = "User or project not found")
    @PostMapping("/unassign")
    @PreAuthorize("@userService.isUserAdminOfProject(#assignUserDto.id, authentication.name)")
    public String unassignUserFromProject(@ModelAttribute AssignUserDto assignUserDto) {
        userService.unassignUser(assignUserDto);
        return "redirect:/api/project/" + assignUserDto.getId();
    }
}
