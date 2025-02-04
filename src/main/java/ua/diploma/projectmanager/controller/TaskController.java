package ua.diploma.projectmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.task.TaskDto;
import ua.diploma.projectmanager.dto.task.TaskFullInfoDto;
import ua.diploma.projectmanager.dto.task.TaskUpdateDto;
import ua.diploma.projectmanager.service.TaskService;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userService.isUserAssignedToTaskProject(#id, authentication.principal.username)")
    public TaskFullInfoDto getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN') or (hasAnyRole('MANAGER', 'DEV') and @userService.isUserAssignedToTaskProject(#taskDto.projectId, authentication.principal.username))")
    public TaskFullInfoDto createTask(@Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @userService.isUserAssignedToTaskProject(#taskUpdateDto.id, authentication.principal.username))")
    public TaskFullInfoDto updateTask(@Valid @RequestBody TaskUpdateDto taskUpdateDto) {
        return taskService.updateTask(taskUpdateDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or (hasRole('MANAGER') and @userService.isUserAssignedToTaskProject(#id, authentication.principal.username))")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
