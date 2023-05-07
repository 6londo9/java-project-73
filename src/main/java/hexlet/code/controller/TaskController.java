package hexlet.code.controller;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;
import hexlet.code.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
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
    public static final String TASK_CONTROLLER_PATH = "/tasks";
    private static final String ONLY_AUTHOR_BY_ID = """
            @taskRepository.findById(#id).get().getAuthor().getEmail() == authentication.getName()
        """;
    private final TaskService taskService;

    @Operation(summary = "Get all tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = Task.class)),
                         description = "Tasks showed"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),
    })
    @GetMapping
    public List<Task> getAllTasks(
            @QuerydslPredicate(root = Task.class)
            Predicate predicate
    ) {
        return taskService.getTasks(predicate);
    }

    @Operation(summary = "Get task by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = Task.class)),
                         description = "Task was found"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "Task with such id is not found")
    })
    @GetMapping(ID)
    public Task getCurrentTask(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(summary = "Create new task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                         content = @Content(schema = @Schema(implementation = Task.class)),
                         description = "Task was successfully created"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "422",
                         content = @Content,
                         description = "Invalid data")
    })
    @PostMapping
    public Task createTask(
            @RequestBody
            @Valid
            TaskDto taskDto
    ) {
        return taskService.createTask(taskDto);
    }

    @Operation(summary = "Update existing task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = Task.class)),
                         description = "Task was successfully updated"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "Task status with such id is not found"),

            @ApiResponse(responseCode = "422",
                         content = @Content,
                         description = "Invalid data")
    })
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

    @Operation(summary = "Delete task")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content,
                         description = "Task was successfully deleted"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "403",
                         content = @Content,
                         description = "Not enough enough rights to delete task"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "Task with such id is not found")
    })
    @DeleteMapping(ID)
    @PreAuthorize(ONLY_AUTHOR_BY_ID)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }
}
