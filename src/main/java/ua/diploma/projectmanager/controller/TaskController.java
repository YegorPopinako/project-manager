package ua.diploma.projectmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.task.TaskDto;
import ua.diploma.projectmanager.dto.task.TaskFullInfoDto;
import ua.diploma.projectmanager.dto.task.TaskUpdateDto;
import ua.diploma.projectmanager.dto.user.AssignUserDto;
import ua.diploma.projectmanager.service.TaskService;
import ua.diploma.projectmanager.service.UserService;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isUserAssignedToProjectByTask(#id, authentication.principal.username)")
    public TaskFullInfoDto getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('MANAGER', 'DEV') and @userService.isUserAssignedToProjectByTask(#taskDto.projectId, authentication.principal.username))")
    public TaskFullInfoDto createTask(@Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @userService.isUserAssignedToProjectByTask(#taskUpdateDto.id, authentication.principal.username))")
    public TaskFullInfoDto updateTask(@Valid @RequestBody TaskUpdateDto taskUpdateDto) {
        return taskService.updateTask(taskUpdateDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @userService.isUserAssignedToProjectByTask(#id, authentication.principal.username))")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @PostMapping("/assign")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @userService.isUserAssignedToProjectByTask(#assignUserDto.id, authentication.principal.username))")
    public ResponseEntity<String> assignUserToTask(@RequestBody AssignUserDto assignUserDto) {
        userService.assignUser(assignUserDto);
        return ResponseEntity.ok("User %s assigned to task %d successfully".formatted(
                assignUserDto.getUserEmail(), assignUserDto.getId()
        ));
    }

    @PostMapping("/unassign")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @userService.isUserAssignedToProjectByTask(#assignUserDto.id, authentication.principal.username))")
    public ResponseEntity<String> unassignUserFromTask(@RequestBody AssignUserDto assignUserDto) {
        userService.unassignUser(assignUserDto);
        return ResponseEntity.ok("User %s unassigned from task %d successfully".formatted(
                assignUserDto.getUserEmail(), assignUserDto.getId()
        ));
    }
}
