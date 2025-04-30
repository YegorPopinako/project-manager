package ua.diploma.projectmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.dto.user.UserUpdateDto;
import ua.diploma.projectmanager.model.Notification;
import ua.diploma.projectmanager.service.NotificationService;
import ua.diploma.projectmanager.service.UserService;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final NotificationService notificationService;

    @GetMapping
    public String getUser(Authentication authentication, Model model) {
        List<Notification> notifications = notificationService.getUserNotifications(authentication.getName());
        UserFullInfoDto userDto = userService.getUser(authentication.getName());
        model.addAttribute("notifications", notifications);
        model.addAttribute("userDto", userDto);
        return "userProfile";
    }

    @PutMapping
    public String updateUser(@ModelAttribute UserUpdateDto userDto, Model model, Authentication authentication) {
        userService.updateUser(userDto);
        UserFullInfoDto updatedUser = userService.getUser(authentication.getName());
        List<Notification> notifications = notificationService.getUserNotifications(authentication.getName());
        model.addAttribute("notifications", notifications);
        model.addAttribute("userDto", updatedUser);
        return "userProfile";
    }
}
