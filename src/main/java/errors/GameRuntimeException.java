package errors;

public class GameRuntimeException extends RuntimeException {
    public GameRuntimeException(String message) {
        super(message);
    }
}
