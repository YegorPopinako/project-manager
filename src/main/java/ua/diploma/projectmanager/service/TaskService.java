package ua.diploma.projectmanager.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import ua.diploma.projectmanager.dto.task.TaskDto;
import ua.diploma.projectmanager.dto.task.TaskFullInfoDto;
import ua.diploma.projectmanager.dto.task.TaskUpdateDto;
import ua.diploma.projectmanager.model.Task;
import ua.diploma.projectmanager.repository.ProjectRepository;
import ua.diploma.projectmanager.repository.TaskRepository;
import ua.diploma.projectmanager.service.mapper.TaskMapper;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;
    private final ModelMapper modelMapper;
    private final TaskMapper taskMapper;

    public TaskFullInfoDto getTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Task with id " + id + " not found"));
        return modelMapper.map(task, TaskFullInfoDto.class);
    }

    @Transactional
    public TaskFullInfoDto createTask(TaskDto taskDto) {
        Task task = new Task();
        task.setTitle(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());
        task.setStatus(taskDto.getStatus());
        task.setEstimate(taskDto.getEstimate());
        task.setCreatedAt(LocalDateTime.now());
        task.setProject(projectRepository.findById(taskDto.getProjectId()).orElseThrow(() ->
                new EntityNotFoundException("Project not found")));
        return modelMapper.map(taskRepository.save(task), TaskFullInfoDto.class);
    }

    @Transactional
    public TaskFullInfoDto updateTask(TaskUpdateDto taskDto) {
        var task = taskRepository.findById(taskDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("Task with id: %s not found".formatted(taskDto.getId())));

        taskMapper.mapTaskFromTaskDto(taskDto, task);
        task.setUpdatedAt(LocalDateTime.now());

        return modelMapper.map(taskRepository.save(task), TaskFullInfoDto.class);
    }

    @Transactional
    public void deleteTask(Long id) {
        getTask(id);
        taskRepository.deleteById(id);
    }
}
