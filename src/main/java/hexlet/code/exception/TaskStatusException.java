package hexlet.code.exception;

import java.util.NoSuchElementException;

public class TaskStatusException extends NoSuchElementException {
    public TaskStatusException(String message) {
        super(message);
    }
}
