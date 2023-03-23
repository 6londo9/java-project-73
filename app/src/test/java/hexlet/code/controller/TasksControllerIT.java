package hexlet.code.controller;

import hexlet.code.config.SpringConfigForIT;
import hexlet.code.repository.TaskRepository;
import hexlet.code.utils.TestUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static hexlet.code.config.SpringConfigForIT.TEST_PROFILE;
//import static hexlet.code.controller.TaskController.TASKS_CONTROLLER_PATH;
//import static hexlet.code.controller.UserController.ID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = SpringConfigForIT.class)
@AutoConfigureMockMvc
@ActiveProfiles(TEST_PROFILE)
public class TasksControllerIT {
    @Autowired
    private TaskRepository taskRepository;
    @Autowired
    private TestUtils testUtils;
    @BeforeEach
    public void beforeEach() {

    }
    @AfterEach
    public void afterEach() {
        testUtils.tearDown();
    }

    @Test
    void testGetAllTasks() {

    }

}
