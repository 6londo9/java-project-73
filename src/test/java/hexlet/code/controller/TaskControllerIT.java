package hexlet.code.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import hexlet.code.config.SpringConfigForIT;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
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

import java.util.HashSet;

import static hexlet.code.utils.TestUtils.BASE_URL;
import static hexlet.code.utils.TestUtils.USER_EMAIL;
import static hexlet.code.utils.TestUtils.asJson;
import static hexlet.code.utils.TestUtils.fromJson;
import static org.assertj.core.api.Assertions.assertThat;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
public class TaskControllerIT {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TestUtils utils;
    @BeforeEach
    public void beforeEach() throws Exception {
        utils.createDefaultTask();
    }
    @AfterEach
    public void afterEach() {
        utils.tearDown();
    }

    @Test
    void testGetAllTasks() throws Exception {
        assertEquals(1, taskRepository.count());

        final var user = userRepository.findAll().get(0);
        final var status = taskStatusRepository.findAll().get(0);
        final var task1 = utils.task("Test", "Testing endpoint", status, user);
        final var task2 = utils.task("Work", "Almost working", status, user);
        taskRepository.save(task1);
        taskRepository.save(task2);

        final var response = utils.perform(
                get(BASE_URL + TASK_CONTROLLER_PATH), USER_EMAIL
        ).andExpect(status().isOk()).andReturn().getResponse();
        assertEquals(3, taskRepository.count());
        assertThat(response.getContentAsString()).contains("Test");
        assertThat(response.getContentAsString()).contains("Work");
        assertThat(response.getContentAsString()).contains("Testing");
    }

    @Test
    void testGetFilteredTasks() throws Exception {
        assertEquals(1, taskRepository.count());

        final var user = userRepository.findAll().get(0);
        final var status = taskStatusRepository.findAll().get(0);
        final var task1 = utils.task("Test", "Testing endpoint", status, user);
        final var task2 = utils.task("Work", "Almost working", status, user);
        taskRepository.save(task1);
        taskRepository.save(task2);

        final var response = utils.perform(get(
                BASE_URL + TASK_CONTROLLER_PATH + "?statuses=" + status.getId()), USER_EMAIL
                )
        .andExpect(status().isOk()).andReturn().getResponse();
        assertThat(response.getContentAsString()).contains("Test");
        assertThat(response.getContentAsString()).contains("Work");
        assertThat(response.getContentAsString()).contains("Testing");
    }
    @Test
    void testGetTaskById() throws Exception {
        final var expectedTask = taskRepository.findAll().get(0);
        final var response = utils.perform(
                get(BASE_URL + TASK_CONTROLLER_PATH + ID, expectedTask.getId()), USER_EMAIL
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(expectedTask.getName(), actualTask.getName());
        assertEquals(expectedTask.getDescription(), actualTask.getDescription());
        assertEquals(expectedTask.getAuthor().getId(), actualTask.getAuthor().getId());
        assertEquals(expectedTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
    }

    @Test
    void testCreateTask() throws Exception {
        final var user = userRepository.findAll().get(0);
        final var status = taskStatusRepository.findAll().get(0);

        final var task = new TaskDto("New task", "Create new task", status.getId(), user.getId(), new HashSet<>());
        final var response = utils.perform(
                post(BASE_URL + TASK_CONTROLLER_PATH)
                        .content(asJson(task))
                        .contentType(APPLICATION_JSON),
                user.getEmail()
        ).andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        assertEquals(2, taskRepository.count());
        assertThat(response.getContentAsString()).contains("New task");
    }

    @Test
    void testUpdateTask() throws Exception {
        final var author = userRepository.findAll().get(0);
        final var userDto = UserDto.builder()
                .firstName("Ignat")
                .lastName("Vasilyev")
                .email("evas@gmail.com")
                .password("password")
                .build();
        utils.registerUser(userDto);
        final var user = userRepository.findByEmail(userDto.getEmail()).get();

        final var taskStatusDto = new TaskStatusDto("Working");
        utils.createTaskStatus(taskStatusDto);
        final var taskStatus = taskStatusRepository.findByName(taskStatusDto.getName()).get();

        final var currentTask = taskRepository.findAll().get(0);
        final var dto = new TaskDto("New name", "New description", taskStatus.getId(), user.getId(), new HashSet<>());

        final var response = utils.perform(
                put(BASE_URL + TASK_CONTROLLER_PATH + ID, currentTask.getId())
                        .content(asJson(dto))
                        .contentType(APPLICATION_JSON),
                author.getEmail()
        ).andExpect(status().isOk())
                .andReturn()
                .getResponse();

        Task actualTask = fromJson(response.getContentAsString(), new TypeReference<>() {
        });

        assertEquals(currentTask.getId(), actualTask.getId());
        assertNotEquals(currentTask.getName(), actualTask.getName());
        assertNotEquals(currentTask.getDescription(), actualTask.getDescription());
        assertNotEquals(currentTask.getExecutor().getId(), actualTask.getExecutor().getId());
        assertNotEquals(currentTask.getTaskStatus().getId(), actualTask.getTaskStatus().getId());
        assertEquals(currentTask.getAuthor().getId(), actualTask.getAuthor().getId());
    }

    @Test
    void testDeleteTask() throws Exception {
        final var author = userRepository.findAll().get(0);
        final var task = taskRepository.findAll().get(0);
        utils.perform(
                delete(BASE_URL + TASK_CONTROLLER_PATH + ID, task.getId()),
                author.getEmail()
        ).andExpect(status().isOk());

        assertThat(taskRepository.existsById(task.getId())).isFalse();
        assertEquals(0, taskRepository.count());
    }

    @Test
    void testDeleteTaskUnauthorized() throws Exception {
        final var newUserDto = UserDto
                .builder()
                .email("newuser@gmail.com")
                .firstName("firstname")
                .lastName("lastname")
                .password("password")
                .build();
        utils.registerUser(newUserDto);

        final var task = taskRepository.findAll().get(0);
        utils.perform(
                delete(BASE_URL + TASK_CONTROLLER_PATH + ID, task.getId()),
                newUserDto.getEmail()
        ).andExpect(status().isForbidden());
    }
}
