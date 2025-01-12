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
import ua.diploma.projectmanager.repository.ProjectRepository;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/clear_h2.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
class ProjectControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProjectRepository projectRepository;

    @Test
    @SneakyThrows
    void getProject() {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        mockMvc.perform(get("/api/project/" + project.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Project 1"))
                .andExpect(jsonPath("$.description").value("Description 1"));
    }

    @Test
    @SneakyThrows
    void getNonexistentProject() {
        mockMvc.perform(get("/api/project/2"))
                .andExpect(status().isNotFound());
    }

    @Test
    @SneakyThrows
    void createProject() {
        String projectJson = """
                    {
                        "title": "test",
                        "description": "This is a new project"
                    }
                """;

        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.title").value("test"))
                .andExpect(jsonPath("$.description").value("This is a new project"));
    }

    @ParameterizedTest(name = "{0} {1}")
    @MethodSource("sourceInvalidProjectCreation")
    @SneakyThrows
    void createProjectWithInvalidData(String title, String description) {
        String projectJson = """
                    {
                        "title": %s,
                        "description": %s
                    }
                """.formatted(title, description);

        mockMvc.perform(post("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> sourceInvalidProjectCreation() {
        return Stream.of(
                Arguments.of("", "NotEmptyDescription"),
                Arguments.of("NotEmptyTitle", "")
        );
    }

    @Test
    @SneakyThrows
    void updateProject() {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        String projectJson = """
                    {
                        "id": %s,
                        "title": "Updated Project",
                        "description": "This is an updated project"
                    }
                """.formatted(project.getId());

        mockMvc.perform(put("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(project.getId()))
                .andExpect(jsonPath("$.title").value("Updated Project"))
                .andExpect(jsonPath("$.description").value("This is an updated project"));
    }

    @Test
    @SneakyThrows
    void updateProjectWithInvalidId() {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        String projectJson = """
                    {
                        "id": %s,
                        "title": "Updated Project",
                        "description": "This is an updated project"
                    }
                """.formatted(project.getId() + 1);

        mockMvc.perform(put("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isNotFound());
    }

    @ParameterizedTest(name = "{0} {1} {2}")
    @MethodSource("sourceInvalidProjectUpdate")
    @SneakyThrows
    void updateProjectWithInvalidData(Long id, String title, String description) {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        String projectJson = """
                    {
                        "id": %s,
                        "title": %s,
                        "description": "%s"
                    }
                """.formatted(id, title, description);

        mockMvc.perform(put("/api/project")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projectJson))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> sourceInvalidProjectUpdate() {
        return Stream.of(
                Arguments.of(null, "NotEmptyTitle", "NotEmptyDescription"),
                Arguments.of("1", "", "NotEmptyDescription"),
                Arguments.of("1", "NotEmptyTitle", "")
        );
    }

    @Test
    @SneakyThrows
    void deleteProject() {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        mockMvc.perform(delete("/api/project/" + project.getId()))
                .andExpect(status().isOk());
    }

    @Test
    @SneakyThrows
    void deleteProjectWithInvalidId() {
        Project project = new Project();
        project.setTitle("Project 1");
        project.setDescription("Description 1");
        projectRepository.save(project);

        mockMvc.perform(delete("/api/project/234"))
                .andExpect(status().isNotFound());
    }
}
