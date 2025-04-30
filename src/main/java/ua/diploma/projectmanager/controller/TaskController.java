package ua.diploma.projectmanager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ua.diploma.projectmanager.dto.task.TaskDto;
import ua.diploma.projectmanager.dto.task.TaskFullInfoDto;
import ua.diploma.projectmanager.dto.task.TaskUpdateDto;
import ua.diploma.projectmanager.dto.task.UpdateTaskStatusDto;
import ua.diploma.projectmanager.dto.user.AssignUserDto;
import ua.diploma.projectmanager.model.Task;
import ua.diploma.projectmanager.repository.TaskRepository;
import ua.diploma.projectmanager.security.enums.TaskStatus;
import ua.diploma.projectmanager.service.TaskService;
import ua.diploma.projectmanager.service.UserService;

@Controller
@RequestMapping("/api/task")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;
    private final UserService userService;
    private final TaskRepository taskRepository;

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
    @PreAuthorize("@userService.isUserAssignedToProject(#taskDto.projectId, authentication.name)")
    public String createTask(@Valid @ModelAttribute TaskDto taskDto) {
        taskService.createTask(taskDto);
        return "redirect:/api/project/" + taskDto.getProjectId();
    }

    @Operation(summary = "Update existing task")
    @ApiResponse(responseCode = "200", description = "Task updated")
    @ApiResponse(responseCode = "400", description = "Invalid task data")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PutMapping()
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#taskUpdateDto.id, authentication.name)")
    public String updateTask(@ModelAttribute @Valid TaskUpdateDto taskUpdateDto) {
        taskService.updateTask(taskUpdateDto);
        return "redirect:/api/project/" + taskUpdateDto.getProjectId();
    }

    @Operation(summary = "Delete task by ID")
    @ApiResponse(responseCode = "200", description = "Task deleted")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#id, authentication.name)")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @Operation(summary = "Assign user to task")
    @ApiResponse(responseCode = "200", description = "User assigned to task")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PostMapping("/assign")
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#assignUserDto.id, authentication.name)")
    public String assignUserToTask(@ModelAttribute AssignUserDto assignUserDto) {
        Task task = taskRepository.findById(assignUserDto.getId()).orElseThrow(() -> new EntityNotFoundException("Task not found"));
        userService.assignUser(assignUserDto);
        return "redirect:/api/project/" + task.getProject().getId();
    }

    @Operation(summary = "Unassign user from task")
    @ApiResponse(responseCode = "200", description = "User unassigned from task")
    @ApiResponse(responseCode = "404", description = "Task not found")
    @PostMapping("/unassign")
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#assignUserDto.id, authentication.name)")
    public String unassignUserFromTask(@ModelAttribute AssignUserDto assignUserDto) {
        Task task = taskRepository.findById(assignUserDto.getId()).orElseThrow(() -> new EntityNotFoundException("Task not found"));
        userService.unassignUser(assignUserDto);
        return "redirect:/api/project/" + task.getProject().getId();
    }

    @PostMapping("/status")
    @PreAuthorize("@userService.isUserAssignedToProjectByTask(#updateTaskStatusDto.taskId, authentication.name)")
    public ResponseEntity<Void> updateTaskStatus(@RequestBody UpdateTaskStatusDto updateTaskStatusDto) {
        Task task = taskRepository.findById(updateTaskStatusDto.getTaskId())
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        task.setStatus(TaskStatus.valueOf(updateTaskStatusDto.getNewStatus()));
        taskRepository.save(task);

        return ResponseEntity.ok().build();
    }
}
