package hexlet.code.exception;

import java.util.NoSuchElementException;

public class UserException extends NoSuchElementException {
    public UserException(String message) {
        super(message);
    }
}
