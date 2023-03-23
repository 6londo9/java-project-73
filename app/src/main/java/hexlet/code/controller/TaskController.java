package hexlet.code.controller;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.repository.TaskRepository;
import hexlet.code.service.TaskService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

import static hexlet.code.controller.TaskController.TASKS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;

@RestController
@AllArgsConstructor
@RequestMapping("${base-url}" + TASKS_CONTROLLER_PATH)
public class TaskController {
    public static final String TASKS_CONTROLLER_PATH = "/tasks";
    private static final String ONLY_AUTHOR_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;
    private final TaskService taskService;
    private final TaskRepository taskRepository;

    @GetMapping
    public List<Task> getAllTasks() {
        return taskService.getTasks();
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
