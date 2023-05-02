package hexlet.code.service;

import com.querydsl.core.types.Predicate;
import hexlet.code.dto.TaskDto;
import hexlet.code.exception.TaskException;
import hexlet.code.model.Task;
import hexlet.code.model.TaskStatus;
import hexlet.code.model.User;
import hexlet.code.repository.TaskRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class TaskServiceImpl implements TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;
    private final TaskStatusService taskStatusService;
    private final LabelService labelService;

    @Override
    public List<Task> getTasks(Predicate predicate) {
        return (List<Task>) taskRepository.findAll(predicate);
    }

    @Override
    public Task getTaskById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskException("Task with id: " + id + " is not found."));
    }

    @Override
    public Task createTask(TaskDto taskDto) {
        Task task = new Task();
        return taskRepository.save(merge(task, taskDto));
    }

    @Override
    public Task updateTask(Long id, TaskDto taskDto) {
        Task taskToUpdate = taskRepository.findById(id)
                .orElseThrow(() -> new TaskException("Task with id: " + id + " is not found."));
        return taskRepository.save(merge(taskToUpdate, taskDto));
    }

    @Override
    public void deleteTask(Long id) {
        taskRepository.deleteById(id);
    }

    private Task merge(Task task, TaskDto dto) {
        User author = userService.getCurrentUser();
        User executor = dto.getExecutorId() != null
                                ? userService.getUserById(dto.getExecutorId()) : null;
        TaskStatus taskStatus = dto.getTaskStatusId() != null
                                ? taskStatusService.getTaskStatus(dto.getTaskStatusId()) : null;

        task.setName(dto.getName());
        task.setDescription(dto.getDescription());
        task.setTaskStatus(taskStatus);
        task.setAuthor(author);
        task.setExecutor(executor);
        task.setLabels(
                dto.getLabelIds().stream()
                        .map(labelService::getLabel)
                        .collect(Collectors.toSet())
        );
        return task;
    }
}
