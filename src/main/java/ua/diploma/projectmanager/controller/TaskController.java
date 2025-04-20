package ua.diploma.projectmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
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
import ua.diploma.projectmanager.security.enums.AssignmentType;
import ua.diploma.projectmanager.service.TaskService;
import ua.diploma.projectmanager.service.UserService;

@RestController
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;

    @Operation(summary = "Get task by ID")
    @ApiResponse(responseCode = "200", description = "Task found")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @GetMapping("/{id}")
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#id, authentication.name)")
    public TaskFullInfoDto getTask(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @Operation(summary = "Create new task")
    @ApiResponse(responseCode = "201", description = "Task created")
    @ApiResponse(responseCode = "400", description = "Invalid task data")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("@userService.isUserAssignedToProject(#taskDto.projectId, authentication.name)")
    public TaskFullInfoDto createTask(@Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Update existing task")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @ApiResponse(responseCode = "400", description = "Invalid task data")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PutMapping
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#taskUpdateDto.id, authentication.name)")
    public TaskFullInfoDto updateTask(@Valid @RequestBody TaskUpdateDto taskUpdateDto) {
        return taskService.updateTask(taskUpdateDto);
    }

    @Operation(summary = "Delete task by ID")
    @ApiResponse(responseCode = "200", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @DeleteMapping("/{id}")
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#id, authentication.name)")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @Operation(summary = "Assign user to task")
    @ApiResponse(responseCode = "200", description = "User assigned to task")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PostMapping("/assign")
    @PreAuthorize("userService.isUserAssignedToProjectByTask(#assignUserDto.id, authentication.name)")
    public ResponseEntity<String> assignUserToTask(@RequestBody AssignUserDto assignUserDto) {
        userService.assignUser(new AssignUserDto(
                assignUserDto.getId(),
                assignUserDto.getUserEmail(),
                AssignmentType.TASK));

        return ResponseEntity.ok("User %s assigned to task %d successfully".formatted(
                assignUserDto.getUserEmail(), assignUserDto.getId()
        ));
    }

    @Operation(summary = "Unassign user from task")
    @ApiResponse(responseCode = "200", description = "User unassigned from task")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PostMapping("/unassign")
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#assignUserDto.id, authentication.name)")
    public ResponseEntity<String> unassignUserFromTask(@RequestBody AssignUserDto assignUserDto) {
        userService.unassignUser(new AssignUserDto(
                assignUserDto.getId(),
                assignUserDto.getUserEmail(),
                AssignmentType.TASK));

        return ResponseEntity.ok("User %s unassigned from task %d successfully".formatted(
                assignUserDto.getUserEmail(), assignUserDto.getId()
        ));
    }
}
