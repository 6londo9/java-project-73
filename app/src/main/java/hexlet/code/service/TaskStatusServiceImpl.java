package hexlet.code.service;

import hexlet.code.dto.TaskStatusDto;
import hexlet.code.exception.TaskStatusException;
import hexlet.code.model.TaskStatus;
import hexlet.code.repository.TaskStatusRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class TaskStatusServiceImpl implements TaskStatusService {
    private final TaskStatusRepository taskRepository;
    @Override
    public TaskStatus createTaskStatus(TaskStatusDto taskStatusDto) {
        TaskStatus taskStatus = new TaskStatus();
        taskStatus.setName(taskStatusDto.getName());
        return taskRepository.save(taskStatus);
    }

    @Override
    public TaskStatus getTaskStatus(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new TaskStatusException("Task with such id not found"));
    }

    @Override
    public List<TaskStatus> getTasks() {
        return taskRepository.findAll();
    }

    @Override
    public void deleteTaskStatus(Long id) {
        taskRepository.deleteById(id);
    }

    @Override
    public TaskStatus updateTaskStatus(Long id, TaskStatusDto taskStatusDto) {
        TaskStatus taskStatusToUpdate = taskRepository.findById(id)
                .orElseThrow(() -> new TaskStatusException("Task with such id not found"));
        taskStatusToUpdate.setName(taskStatusDto.getName());
        return taskRepository.save(taskStatusToUpdate);
    }

    @Override
    public TaskStatus findByName(String name) {
        return taskRepository.findByName(name)
                .orElseThrow(() -> new TaskStatusException("Task with such name is not found"));
    }
}
