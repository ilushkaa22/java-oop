package exceptions;

/** Непроверяемое исключение: данные книги некорректны. */
public class InvalidBookDataException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    /**
     * Создаёт исключение с описанием ошибки.
     * @param message описание ошибки
     */
    public InvalidBookDataException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение, сохраняя исходную причину.
     * @param message описание ошибки
     * @param cause исходное исключение
     */
    public InvalidBookDataException(String message, Throwable cause) {
        super(message, cause);
    }
}
