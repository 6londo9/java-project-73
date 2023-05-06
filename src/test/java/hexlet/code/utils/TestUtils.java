package hexlet.code.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hexlet.code.component.JWTHelper;
import hexlet.code.dto.LabelDto;
import hexlet.code.dto.TaskDto;
import hexlet.code.dto.TaskStatusDto;
import hexlet.code.dto.UserDto;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.LabelRepository;
import hexlet.code.repository.TaskRepository;
import hexlet.code.repository.TaskStatusRepository;
import hexlet.code.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashSet;
import java.util.Map;

import static hexlet.code.controller.UserController.USER_CONTROLLER_PATH;
import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.LabelController.LABEL_CONTROLLER_PATH;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@Component
public class TestUtils {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TaskStatusRepository taskStatusRepository;
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private LabelRepository labelRepository;
    @Autowired
    private JWTHelper jwtHelper;

    public void tearDown() {
        taskRepository.deleteAll();
        userRepository.deleteAll();
        taskStatusRepository.deleteAll();
        labelRepository.deleteAll();
    }

    public static final String USER_EMAIL = "ivanov@email.ru";

    /**
     *  User utils
     */
    private final UserDto testUserDto = new UserDto(
            "Ivan", "Ivanov", "ivanov@email.ru", "ivanov"
    );
    public UserDto getDefaultUser() {
        return testUserDto;
    }
    public ResultActions registerDefaultUser() throws Exception {
        return registerUser(testUserDto);
    }
    public ResultActions registerUser(final UserDto userDto) throws Exception {
        final var request = post(USER_CONTROLLER_PATH)
                .content(asJson(userDto))
                .contentType(APPLICATION_JSON);
        return perform(request);
    }

    /**
     *  Task utils
     */
    public ResultActions createDefaultTask() throws Exception {
        createDefaultTaskStatus();
        User user = userRepository.findAll().get(0);
        TaskStatus taskStatus = taskStatusRepository.findAll().get(0);
        TaskDto dto = new TaskDto("Testing",
                "Test task endpoints",
                taskStatus.getId(),
                user.getId(),
                new HashSet<>());

        final var request = post(TASK_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);
        return perform(request, USER_EMAIL);
    }

    public ResultActions getTasks() throws Exception {
        final var request = get(TASK_CONTROLLER_PATH);
        return perform(request);
    }

    public Task task(String name, String description, TaskStatus status, User user) {
        Task task = new Task();
        task.setName(name);
        task.setDescription(description);
        task.setTaskStatus(status);
        task.setExecutor(user);
        return task;
    }

    /**
     *  Task status utils
     */
    private final TaskStatusDto taskStatusDto = new TaskStatusDto(
            "Testing"
    );

    public ResultActions createDefaultTaskStatus() throws Exception {
        registerDefaultUser();
        final var request = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(taskStatusDto))
                .contentType(APPLICATION_JSON);
        return perform(request, USER_EMAIL);
    }

    public ResultActions getTaskStatuses() throws Exception {
        final var request = get(TASK_STATUS_CONTROLLER_PATH);
        return perform(request);
    }

    public ResultActions createTaskStatus(TaskStatusDto dto) throws Exception {
        final var request = post(TASK_STATUS_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);
        return perform(request, USER_EMAIL);
    }

    /**
     *  Label utils
     */
    private final LabelDto labelDto = new LabelDto("Bug");
    public ResultActions createDefaultLabel() throws Exception {
        registerDefaultUser();
        final var request = post(LABEL_CONTROLLER_PATH)
                .content(asJson(labelDto))
                .contentType(APPLICATION_JSON);
        return perform(request, USER_EMAIL);
    }
    public ResultActions createLabel(LabelDto dto) throws Exception {
        final var request = post(LABEL_CONTROLLER_PATH)
                .content(asJson(dto))
                .contentType(APPLICATION_JSON);
        return perform(request, USER_EMAIL);
    }

    /**
     *  Request utils
     */
    public ResultActions perform(final MockHttpServletRequestBuilder request, final String byUser) throws Exception {
        final String token = jwtHelper.expiring(Map.of("username", byUser));
        request.header(AUTHORIZATION, token);

        return perform(request);
    }

    public ResultActions perform(final MockHttpServletRequestBuilder request) throws Exception {
        return mockMvc.perform(request);
    }

    private static final ObjectMapper MAPPER = new ObjectMapper();
    public static String asJson(Object obj) throws JsonProcessingException {
        return MAPPER.writeValueAsString(obj);
    }
    public static <T> T fromJson(final String json, final TypeReference<T> to) throws JsonProcessingException {
        return MAPPER.readValue(json, to);
    }
}
