package hexlet.code.exception;

import java.util.NoSuchElementException;

public class TaskException extends NoSuchElementException {
    public TaskException(String message) {
        super(message);
    }
}
