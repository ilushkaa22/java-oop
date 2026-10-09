import java.time.Year;

/**
 * Представляет книгу с идентификатором, названием, автором и годом издания.
 */
public class Book {
    private static int counter;

    private long id;
    private String title;
    private String author;
    private int year;

    /** Создаёт книгу с незаданными идентификатором и годом. */
    public Book() {
        this(0, "Без названия", "Неизвестен", 0);
    }

    /**
     * Создаёт книгу без идентификатора и года издания.
     * @param title название книги
     * @param author автор книги
     */
    public Book(String title, String author) {
        this(0, title, author, 0);
    }

    /**
     * Создаёт книгу без идентификатора.
     * @param title название книги
     * @param author автор книги
     * @param year год издания от 0 до текущего года
     */
    public Book(String title, String author, int year) {
        this(0, title, author, year);
    }

    /**
     * Создаёт книгу и проверяет переданные значения через сеттеры.
     *
     * @param id неотрицательный идентификатор, 0 означает, что он не задан
     * @param title название книги
     * @param author автор книги
     * @param year год издания от 0 до текущего года
     */
    public Book(long id, String title, String author, int year) {
        setId(id);
        setTitle(title);
        setAuthor(author);
        setYear(year);
        counter++;
    }

    /**
     * Возвращает число успешно созданных книг за время работы JVM.
     * @return количество созданных объектов, независимо от способа создания
     */
    public static int getCounter() {
        return counter;
    }

    /**
     * Создаёт книгу с ID, равным порядковому номеру создания объекта.
     * Явно заданные ID конструкторов и сеттера не проверяются на уникальность.
     * Предназначен для однопоточного учебного приложения.
     * @param title название книги
     * @param author автор книги
     * @param year год издания от 0 до текущего года
     * @return новая книга с автоматически назначенным идентификатором
     * @throws IllegalArgumentException если данные книги некорректны
     */
    public static Book createBook(String title, String author, int year) {
        return new Book((long) counter + 1, title, author, year);
    }

    /**
     * Возвращает идентификатор книги.
     *
     * @return идентификатор книги
     */
    public long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор книги.
     *
     * @param id неотрицательный идентификатор (0 — не задан)
     * @throws IllegalArgumentException если идентификатор отрицательный
     */
    public void setId(long id) {
        if (id < 0) {
            throw new IllegalArgumentException("Идентификатор не может быть отрицательным");
        }
        this.id = id;
    }

    /**
     * Возвращает название книги.
     *
     * @return название книги
     */
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает название книги.
     *
     * @param title непустое название
     * @throws IllegalArgumentException если название равно {@code null} или пусто
     */
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.title = title;
    }

    /**
     * Возвращает автора книги.
     *
     * @return автор книги
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Устанавливает автора книги.
     *
     * @param author непустое имя автора
     * @throws IllegalArgumentException если автор равен {@code null} или пуст
     */
    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Автор не может быть пустым");
        }
        this.author = author;
    }

    /**
     * Возвращает год издания книги.
     *
     * @return год издания
     */
    public int getYear() {
        return year;
    }

    /**
     * Устанавливает год издания книги.
     *
     * @param year год от 0 до текущего включительно
     * @throws IllegalArgumentException если год находится вне допустимого диапазона
     */
    public void setYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < 0 || year > currentYear) {
            throw new IllegalArgumentException("Год должен быть в диапазоне от 0 до " + currentYear);
        }
        this.year = year;
    }

    /**
     * Формирует полное описание книги.
     *
     * @return описание в формате «Название» — Автор (год)
     */
    public String getDescription() {
        return "\"%s\" — %s (%d)".formatted(title, author, year);
    }

    /**
     * Формирует описание книги в выбранном формате.
     * @param shortFormat true — только название и год, false — полное описание
     * @return описание книги
     */
    public String getDescription(boolean shortFormat) {
        return shortFormat ? "%s (%d)".formatted(title, year) : getDescription();
    }

    /**
     * Возвращает строковое представление книги.
     *
     * @return краткое описание книги
     */
    @Override
    public String toString() {
        return getDescription();
    }
}
