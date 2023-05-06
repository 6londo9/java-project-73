package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.querydsl.binding.QuerydslPredicate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

import static hexlet.code.controller.TaskController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
public class TaskController {
    private final Logger logger = LoggerFactory.getLogger(TaskController.class);
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    private static final String ONLY_AUTHOR_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;
    private final TaskService taskService;

    @GetMapping
    public List<Task> getAllTasks(
            @QuerydslPredicate(root = Task.class,
                               bindings = TaskRepository.class)
            Predicate predicate
    ) {
        logger.info("PREDICATE FOR TASKS IS: " + predicate.toString());
        return taskService.getTasks(predicate);
    }
    @GetMapping(ID)
    public Task getCurrentTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }
    @PostMapping
    public Task createTask(
            @RequestBody
            @Valid
            TaskDto taskDto
    ) {
        return taskService.createTask(taskDto);
    }
    @PutMapping(ID)
    public Task updateTask(
            @PathVariable
            Long id,
            @RequestBody
            @Valid
            TaskDto taskDto
    ) {
        return taskService.updateTask(id, taskDto);
    }
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
