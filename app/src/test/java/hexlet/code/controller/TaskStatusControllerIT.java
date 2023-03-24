package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.assertj.core.api.Assertions.assertThat;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
public class TaskStatusControllerIT {
    @Autowired
    private TestUtils utils;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void beforeEach() throws Exception {
        utils.createDefaultTaskStatus();
    }
    @AfterEach
    public void afterEach() {
        utils.tearDown();
    }

    @Test
    void getTasks() throws Exception {
        assertEquals(1, taskStatusRepository.count());

        final var taskStatus1 = new TaskStatus();
        taskStatus1.setName("Making");
        taskStatusRepository.save(taskStatus1);
        assertEquals(2, taskStatusRepository.count());

        final var taskStatus2 = new TaskStatus();
        taskStatus2.setName("Baking");
        taskStatusRepository.save(taskStatus2);
        assertEquals(3, taskStatusRepository.count());

        final var response = utils.getTaskStatuses().andExpect(status().isOk()).andReturn().getResponse();
        assertThat(response.getContentAsString()).contains("Testing");
        assertThat(response.getContentAsString()).contains("Making");
        assertThat(response.getContentAsString()).contains("Baking");
    }

    @Test
    void getTaskById() throws Exception {
        TaskStatus expectedTask = taskStatusRepository.findAll().get(0);
        final var response = utils.perform(get(TASK_STATUS_CONTROLLER_PATH + ID, expectedTask.getId()))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();
        TaskStatus actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });
        assertEquals(expectedTask.getId(), actualTask.getId());
        assertEquals(expectedTask.getName(), actualTask.getName());
    }

    @Test
    void testCreateTask() throws Exception {
        final var user = userRepository.findAll().get(0);
        TaskStatusDto dto = new TaskStatusDto("Creation test");
        final var response = utils.perform(
                post(TASK_STATUS_CONTROLLER_PATH)
                        .content(asJson(dto))
                        .contentType(APPLICATION_JSON),
                        user.getEmail())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        assertEquals(2, taskStatusRepository.count());
        assertThat(response.getContentAsString()).contains("Creation test");
    }

    @Test
    void testDeleteTask() throws Exception {
        final var taskStatus = taskStatusRepository.findAll().get(0);
        final var user = userRepository.findAll().get(0);
        utils.perform(delete(TASK_STATUS_CONTROLLER_PATH + ID, taskStatus.getId()), user.getEmail())
                .andExpect(status().isOk());
        assertEquals(0, taskStatusRepository.count());
    }

    @Test
    void testUpdateTaskStatus() throws Exception {
        assertEquals(1, taskStatusRepository.count());
        final var user = userRepository.findAll().get(0);

        final var taskStatus = taskStatusRepository.findAll().get(0);
        final var newTask = new TaskStatusDto("New name");

        utils.perform(put(TASK_STATUS_CONTROLLER_PATH + ID, taskStatus.getId())
                .content(asJson(newTask))
                .contentType(APPLICATION_JSON), user.getEmail())
                .andExpect(status().isOk());
        assertTrue(taskStatusRepository.existsById(taskStatus.getId()));
        assertNull(taskStatusRepository.findByName("Testing").orElse(null));
        assertNotNull(taskStatusRepository.findByName("New name"));
    }
}
