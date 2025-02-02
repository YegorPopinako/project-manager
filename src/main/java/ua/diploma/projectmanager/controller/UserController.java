package ua.diploma.projectmanager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.user.AssignUserDto;
import ua.diploma.projectmanager.dto.user.UserFullInfoDto;
import ua.diploma.projectmanager.dto.user.UserUpdateDto;
import ua.diploma.projectmanager.service.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserFullInfoDto getUser(@PathVariable Long id) {
        return userService.getUser(id);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserFullInfoDto updateUser(@RequestBody UserUpdateDto userDto) {
        return userService.updateUser(userDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> assignUserToProject(@RequestBody AssignUserDto assignUserDto) {
        userService.assignUserToProject(assignUserDto);
        return ResponseEntity.ok("User %s assigned to project successfully".formatted(assignUserDto.getUserEmail()));
    }
}
