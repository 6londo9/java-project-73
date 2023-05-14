package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.service.TaskStatusService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_STATUS_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;

@RestController
@RequestMapping("${base-url}" + TASK_STATUS_CONTROLLER_PATH)
@AllArgsConstructor
public class TaskStatusController {
    public static final String TASK_STATUS_CONTROLLER_PATH = "/statuses";
    private final TaskStatusService taskService;

    @Operation(summary = "Get all task statuses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = TaskStatus.class)),
                         description = "Task statuses showed"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),
    })
    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        return taskService.getTasks();
    }

    @Operation(summary = "Get task status by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = TaskStatus.class)),
                         description = "Task status was found"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "Task status with such id is not found")
    })
    @GetMapping(ID)
    public TaskStatus getTaskStatus(
            @PathVariable
            Long id
    ) {
       return taskService.getTaskStatus(id);
    }

    @Operation(summary = "Create new task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201",
                         content = @Content(schema = @Schema(implementation = TaskStatus.class)),
                         description = "Task status was successfully created"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "422",
                         content = @Content,
                         description = "Invalid data")
    })
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public TaskStatus createTaskStatus(
            @Valid
            @RequestBody
            TaskStatusDto dto
    ) {
        return taskService.createTaskStatus(dto);
    }

    @Operation(summary = "Update existing task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content(schema = @Schema(implementation = TaskStatus.class)),
                         description = "Task status was successfully updated"),

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
    public TaskStatus updateTaskStatus(
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            TaskStatusDto dto
    ) {
        return taskService.updateTaskStatus(id, dto);
    }

    @Operation(summary = "Delete task status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200",
                         content = @Content,
                         description = "Task status was successfully deleted"),

            @ApiResponse(responseCode = "401",
                         content = @Content,
                         description = "Not authorized request"),

            @ApiResponse(responseCode = "404",
                         content = @Content,
                         description = "Task status with such id is not found")
    })
    @DeleteMapping(ID)
    public void deleteTaskStatus(
            @PathVariable
            Long id
    ) {
        taskService.deleteTaskStatus(id);
    }
}
