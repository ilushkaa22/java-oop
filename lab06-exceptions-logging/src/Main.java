import exceptions.BookNotFoundException;
import exceptions.InvalidBookDataException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** Демонстрация исключений, логирования и настоящей работы с файлами. */
public final class Main {
    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final long MISSING_ID = 999L;
    private static final int BOOK_FIELD_COUNT = 3;

    private Main() { }

    /**
     * Запускает демонстрацию из корня проекта.
     * @param args необязательный путь к файлу книг
     */
    public static void main(String[] args) {
        Repository<Book> repository = new Repository<>();
        System.out.println("=== Сохранение, поиск и удаление ===");
        demonstrateRepository(repository);
        System.out.println("\n=== Проверка данных ===");
        demonstrateValidation();
        System.out.println("\n=== Загрузка книг из файла ===");
        Path input = Path.of(args.length == 0 ? "data/books.txt" : args[0]);
        loadBooks(input, repository);
        System.out.println("\n=== Обработка отсутствующего файла ===");
        loadBooks(Path.of("data/missing-books.txt"), repository);
        System.out.println("\n=== Запись и чтение учебного лога ===");
        demonstrateFileLog();
        System.out.println("Книг в репозитории: " + repository.findAll().size());
        LOGGER.info("Демонстрация завершена");
    }

    private static void demonstrateRepository(Repository<Book> repository) {
        long id = repository.save(new Book("Евгений Онегин", "Александр Пушкин", 1833));
        try {
            System.out.println("Найдена: " + repository.findById(id));
            repository.delete(id);
            repository.findById(MISSING_ID);
        } catch (BookNotFoundException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        } finally {
            System.out.println("finally: демонстрация поиска завершена");
        }
        try {
            repository.delete(MISSING_ID);
        } catch (BookNotFoundException e) {
            System.out.println("Удаление: " + e.getMessage());
        }
    }

    private static void demonstrateValidation() {
        Book book = new Book("Пример", "Автор", 2000);
        try {
            book.setYear(0);
        } catch (InvalidBookDataException e) {
            System.out.println(e.getClass().getSimpleName() + ": " + e.getMessage());
        }
        System.out.println("Год после отклонённого изменения: " + book.getYear());
        try {
            new Book(" ", "Автор", 2000);
        } catch (InvalidBookDataException e) {
            System.out.println("Создание: " + e.getMessage());
        }
    }

    // Доступ без public позволяет тестировать обработку файлов без лишнего класса.
    static void loadBooks(Path path, Repository<Book> repository) {
        int loaded = 0;
        int rejected = 0;
        int lineNumber = 0;
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    repository.save(parseBook(line));
                    loaded++;
                } catch (InvalidBookDataException e) {
                    rejected++;
                    LOGGER.error("Отклонена строка {} файла {}: {}", lineNumber, path, e.getMessage(), e);
                }
            }
        } catch (NoSuchFileException e) {
            LOGGER.error("Файл книг не найден: {}", path, e);
        } catch (IOException e) {
            LOGGER.error("Ошибка чтения файла книг: {}", path, e);
        } finally {
            LOGGER.info("Загрузка завершена: файл={}, загружено={}, отклонено={}", path, loaded, rejected);
        }
    }

    static Book parseBook(String line) {
        String[] fields = line.split(";", -1);
        if (fields.length != BOOK_FIELD_COUNT) {
            throw new InvalidBookDataException("Ожидались три поля: title;author;year");
        }
        try {
            return new Book(fields[0].strip(), fields[1].strip(), Integer.parseInt(fields[2].strip()));
        } catch (NumberFormatException e) {
            // Сохраняем исходную причину ошибки преобразования года.
            throw new InvalidBookDataException("Год должен быть целым числом: " + fields[2], e);
        }
    }

    private static void demonstrateFileLog() {
        Path path = Path.of("logs/file-demo.log");
        try {
            Files.createDirectories(path.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING)) {
                writer.write("INFO: учебная запись в файл");
                writer.newLine();
            }
            try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
                System.out.println("Прочитано из " + path + ": " + reader.readLine());
            }
        } catch (IOException e) {
            LOGGER.error("Не удалось записать или прочитать учебный лог: {}", path, e);
        }
    }
}
