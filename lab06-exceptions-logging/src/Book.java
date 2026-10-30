import exceptions.InvalidBookDataException;
import java.time.Year;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Книга с проверкой данных при создании и изменении. */
public final class Book {
    private static final Logger LOGGER = LoggerFactory.getLogger(Book.class);
    private String title;
    private String author;
    private int year;

    /**
     * Создаёт книгу, проверяя все поля через сеттеры.
     * @param title название
     * @param author автор
     * @param year год издания
     * @throws InvalidBookDataException если данные некорректны
     */
    public Book(String title, String author, int year) {
        setTitle(title);
        setAuthor(author);
        setYear(year);
    }

    /** @return название книги */
    public String getTitle() { return title; }

    /**
     * Меняет название после проверки.
     * @param title непустое название
     * @throws InvalidBookDataException если название null или состоит из пробелов
     */
    public void setTitle(String title) {
        validateText(title, "title");
        this.title = title;
    }

    /** @return автор книги */
    public String getAuthor() { return author; }

    /**
     * Меняет автора после проверки.
     * @param author непустое имя автора
     * @throws InvalidBookDataException если автор null или состоит из пробелов
     */
    public void setAuthor(String author) {
        validateText(author, "author");
        this.author = author;
    }

    /** @return год издания */
    public int getYear() { return year; }

    /**
     * Меняет год после проверки.
     * @param year год от 1 до текущего включительно
     * @throws InvalidBookDataException если год вне диапазона
     */
    public void setYear(int year) {
        int currentYear = Year.now().getValue();
        if (year <= 0 || year > currentYear) {
            String message = "year должен быть от 1 до " + currentYear + ": " + year;
            LOGGER.error("Ошибка валидации: {}", message);
            throw new InvalidBookDataException(message);
        }
        this.year = year;
    }

    private static void validateText(String value, String field) {
        if (value == null || value.isBlank()) {
            String message = field + " не может быть null или пустым";
            LOGGER.error("Ошибка валидации: {}", message);
            throw new InvalidBookDataException(message);
        }
    }

    /** @return краткое описание книги */
    @Override
    public String toString() {
        return title + " — " + author + " (" + year + ")";
    }
}
