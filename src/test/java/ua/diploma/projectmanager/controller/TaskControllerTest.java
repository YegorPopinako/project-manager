package ua.diploma.projectmanager.controller;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ua.diploma.projectmanager.model.Project;
import ua.diploma.projectmanager.model.Task;
import ua.diploma.projectmanager.repository.ProjectRepository;
import ua.diploma.projectmanager.repository.TaskRepository;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clear_h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @SneakyThrows
    void getTask() {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Description 1");
        task.setProject(project);
        task.setStatus("TODO");
        task.setEstimate(1);
        taskRepository.save(task);

        mockMvc.perform(get("/api/task/" + task.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("Task 1"))
                .andExpect(jsonPath("$.description").value("Description 1"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.estimate").value(1))
                .andExpect(jsonPath("$.project.id").value(project.getId()));
    }

    @Test
    @SneakyThrows
    void getNonexistentTask() {
        mockMvc.perform(get("/api/task/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createTask() {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        String taskJson = """
                    {
                             "title": "test",
                             "description": "This is a new task",
                             "status": "TODO",
                             "projectId": %s,
                             "estimate": 1
                    }
                """.formatted(project.getId());

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("test"))
                .andExpect(jsonPath("$.description").value("This is a new task"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.estimate").value(1))
                .andExpect(jsonPath("$.project.id").value(project.getId()));
    }

    @Test
    @SneakyThrows
    void createTaskWithNonexistentProject() {
        String taskJson = """
                    {
                             "title": "test",
                             "description": "This is a new task",
                             "status": "TODO",
                             "projectId": %s,
                             "estimate": 1
                    }
                """.formatted(1L);

        mockMvc.perform(post("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "{0} {1} {2} {3} {4}")
    @MethodSource("sourceInvalidTaskCreation")
    @SneakyThrows
    void createTaskWithInvalidData(String title, String description, String status, Long projectId, Integer estimate) {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        String taskJson = """
                {
                        "title": "%s",
                        "description": "%s",
                        "status": "%s",
                        "projectId": %s,
                        "estimate": %s
                }
                """.formatted(title, description, status, projectId, estimate);

        mockMvc.perform(post("/api/task")
                .contentType(MediaType.APPLICATION_JSON)
                .content(taskJson))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> sourceInvalidTaskCreation() {
        return Stream.of(
                Arguments.of("", "Description", "TODO", 1L, 1),
                Arguments.of("Title", "", "TODO", 1L, 1),
                Arguments.of("Title", "Description", "", 1L, 1),
                Arguments.of("Title", "Description", "TODO", null, 1),
                Arguments.of("Title", "Description", "TODO", 1L, null)
        );
    }

    @Test
    @SneakyThrows
    void updateTask(){
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        Project project2 = new Project();
        project2.setTitle("Project 2");
        project2.setDescription("Description 2");
        projectRepository.save(project2);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Description 1");
        task.setProject(project);
        task.setStatus("TODO");
        task.setEstimate(1);
        taskRepository.save(task);

        String taskJson = """
                    {
                             "id": %s,
                             "title": "test",
                             "description": "This is an updated task",
                             "status": "TODO",
                             "projectId": %s,
                             "estimate": 1
                    }
                """.formatted(task.getId(), project2.getId());

        mockMvc.perform(put("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(task.getId()))
                .andExpect(jsonPath("$.title").value("test"))
                .andExpect(jsonPath("$.description").value("This is an updated task"))
                .andExpect(jsonPath("$.status").value("TODO"))
                .andExpect(jsonPath("$.estimate").value(1))
                .andExpect(jsonPath("$.project.id").value(project2.getId()));
    }

    @Test
    @SneakyThrows
    void updateTaskWithInvalidId(){
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Description 1");
        task.setProject(project);
        task.setStatus("TODO");
        task.setEstimate(1);
        taskRepository.save(task);

        String taskJson = """
                    {
                             "id": %s,
                             "title": "test",
                             "description": "This is an updated task",
                             "status": "TODO",
                             "projectId": 1,
                             "estimate": 1
                    }
                """.formatted(task.getId()+1);

        mockMvc.perform(put("/api/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(taskJson))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void deleteTask(){
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        Task task = new Task();
        task.setTitle("Task 1");
        task.setDescription("Description 1");
        task.setProject(project);
        task.setStatus("TODO");
        task.setEstimate(1);
        taskRepository.save(task);

        mockMvc.perform(delete("/api/task/" + task.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void deleteNonexistentTask(){
        mockMvc.perform(delete("/api/task/2"))
                .andExpect(status().isNotFound());
    }
}