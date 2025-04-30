package ua.diploma.projectmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ua.diploma.projectmanager.dto.project.ProjectDto;
import ua.diploma.projectmanager.dto.project.ProjectFullInfoDto;
import ua.diploma.projectmanager.model.Notification;
import ua.diploma.projectmanager.service.NotificationService;
import ua.diploma.projectmanager.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class UiController {

    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        List<ProjectFullInfoDto> projects = userService.getUserProjects(authentication.getName());
        List<Notification> notifications = notificationService.getUserNotifications(authentication.getName());
        model.addAttribute("notifications", notifications);
        model.addAttribute("username", authentication.getName());
        model.addAttribute("projects", projects);
        model.addAttribute("projectDto", new ProjectDto());
        return "home";
    }
}
