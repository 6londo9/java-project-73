package hexlet.code.exception;

public class TaskStatusException extends RuntimeException {
    public TaskStatusException(String message) {
        super(message);
    }
}
