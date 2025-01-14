package ua.diploma.projectmanager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    public TaskFullInfoDto getProject(@PathVariable Long id) {
        return taskService.getTask(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public TaskFullInfoDto createTask(@Valid @RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }

    @PutMapping
    public TaskFullInfoDto updateTask(@Valid @RequestBody TaskUpdateDto taskUpdateDto) {
        return taskService.updateTask(taskUpdateDto);
    }

    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
