package hexlet.code.controller;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.UserRepository;
import hexlet.code.service.TaskStatusService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;

import java.util.List;

import static hexlet.code.controller.TaskStatusController.TASK_CONTROLLER_PATH;
import static hexlet.code.controller.UserController.ID;

@RestController
@RequestMapping("${base-url}" + TASK_CONTROLLER_PATH)
@AllArgsConstructor
public class TaskStatusController {
    public static final String TASK_CONTROLLER_PATH = "/statuses";
//    private static final String ONLY_USER_BY_ID = """
//            @userRepository.findById(#id).get().getEmail() == authentication.getName()
//        """;
    private final TaskStatusService taskService;
    private final UserRepository userRepository;

    @GetMapping
    public List<TaskStatus> getAllTaskStatuses() {
        return taskService.getTasks();
    }

    @GetMapping(ID)
    public TaskStatus getTaskStatus(
            @PathVariable
            Long id
    ) {
       return taskService.getTaskStatus(id);
    }

    @PostMapping
//    @PreAuthorize(ONLY_USER_BY_ID)
    public TaskStatus createTaskStatus(
            @Valid
            @RequestBody
            TaskStatusDto dto
    ) {
        return taskService.createTaskStatus(dto);
    }

    @PutMapping(ID)
//    @PreAuthorize(ONLY_USER_BY_ID)
    public TaskStatus updateTaskStatus(
            @PathVariable
            Long id,
            @Valid
            @RequestBody
            TaskStatusDto dto
    ) {
        return taskService.updateTaskStatus(id, dto);
    }

    @DeleteMapping(ID)
//    @PreAuthorize(ONLY_USER_BY_ID)
    public void deleteTaskStatus(
            @PathVariable
            Long id
    ) {
        taskService.deleteTaskStatus(id);
    }
}