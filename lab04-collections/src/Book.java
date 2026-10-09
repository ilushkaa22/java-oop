import java.time.Year;
import java.util.Objects;

/** Неизменяемая книга; равенство определяется всеми четырьмя полями. */
public final class Book {
    private final long id;
    private final String title;
    private final String author;
    private final int year;

    /**
     * Создаёт книгу.
     * @param id неотрицательный идентификатор
     * @param title непустое название
     * @param author непустое имя автора
     * @param year год от 0 до текущего включительно
     * @throws IllegalArgumentException если данные некорректны
     */
    public Book(long id, String title, String author, int year) {
        if (id < 0) {
            throw new IllegalArgumentException("Идентификатор не может быть отрицательным");
        }
        if (year < 0 || year > Year.now().getValue()) {
            throw new IllegalArgumentException("Год должен быть от 0 до текущего года");
        }
        this.id = id;
        this.title = requireText(title, "Название");
        this.author = requireText(author, "Автор");
        this.year = year;
    }

    private static String requireText(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " не может быть пустым");
        }
        return value;
    }

    /**
     * Возвращает идентификатор.
     * @return идентификатор книги
     */
    public long getId() { return id; }

    /**
     * Возвращает название.
     * @return название книги
     */
    public String getTitle() { return title; }

    /**
     * Возвращает автора.
     * @return автор книги
     */
    public String getAuthor() { return author; }

    /**
     * Возвращает год издания.
     * @return год издания
     */
    public int getYear() { return year; }

    /**
     * Сравнивает все поля, включая идентификатор.
     * @param other сравниваемый объект
     * @return true, если значения всех полей совпадают
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Book book = (Book) other;
        return id == book.id && year == book.year
                && Objects.equals(title, book.title) && Objects.equals(author, book.author);
    }

    /** @return хеш по тем же полям, которые участвуют в equals */
    @Override
    public int hashCode() {
        return Objects.hash(id, title, author, year);
    }

    /** @return описание книги с идентификатором */
    @Override
    public String toString() {
        return "#%d: \"%s\" — %s (%d)".formatted(id, title, author, year);
    }
}
