package exceptions;

/** Проверяемое исключение: книга с указанным ключом не найдена. */
public class BookNotFoundException extends Exception {
    private static final long serialVersionUID = 1L;

    /**
     * Создаёт исключение с описанием ошибки.
     * @param message описание ошибки
     */
    public BookNotFoundException(String message) {
        super(message);
    }

    /**
     * Создаёт исключение, сохраняя исходную причину.
     * @param message описание ошибки
     * @param cause исходное исключение
     */
    public BookNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
