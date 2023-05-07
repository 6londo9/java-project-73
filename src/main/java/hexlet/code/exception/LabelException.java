package hexlet.code.exception;

import java.util.NoSuchElementException;

public class LabelException extends NoSuchElementException {
    public LabelException(String message) {
        super(message);
    }
}
