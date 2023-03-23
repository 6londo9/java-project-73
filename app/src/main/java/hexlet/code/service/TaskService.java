package hexlet.code.service;

import hexlet.code.dto.TaskDto;
import hexlet.code.model.Task;

import java.util.List;

public interface TaskService {
    List<Task> getTasks();
    Task getTaskById(Long id);
    Task createTask(TaskDto taskDto);
    Task updateTask(Long id, TaskDto taskDto);
    void deleteTask(Long id);
}
