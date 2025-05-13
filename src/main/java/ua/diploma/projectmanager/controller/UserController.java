package ua.diploma.projectmanager.controller;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
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

    @GetMapping("/{id}/image")
    public ResponseEntity<byte[]> getUserImage(@PathVariable Long id) {
        try {
            byte[] imageData = userService.getUserImage(id);

            if (imageData == null || imageData.length == 0) {
                return ResponseEntity.notFound().build();
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_JPEG);
            return new ResponseEntity<>(imageData, headers, HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
