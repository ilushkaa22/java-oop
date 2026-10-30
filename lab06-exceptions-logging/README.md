# Отчёт по лабораторной работе №6

### 1. Титульный лист

**Лабораторная работа №6**  
по дисциплине: «Объектно-ориентированное программирование»  
на тему: «Исключения и логирование»

**Выполнил:**  
Студент группы ИИ21/1  
Саврико Илья

---

### 2. Цель и задачи работы

**Цель:** освоить обработку исключений Java, создание собственных исключений и логирование для диагностики работы приложения.

**Задачи:**

1. Изучить иерархию `Throwable`, `Exception`, `RuntimeException`, `Error`.
2. Создать checked-исключение `BookNotFoundException` и unchecked-исключение `InvalidBookDataException`, оба с поддержкой причины.
3. Доработать `Repository<Book>`: исключения при отсутствии книги, INFO при сохранении и удалении, WARN при неудачном поиске.
4. Проверять название, автора и год книги в сеттерах; при ошибках записывать ERROR.
5. Подключить SLF4J и Logback, настроить консоль и файлы журналов.
6. Продемонстрировать `try-catch-finally`, `try-with-resources`, настоящую загрузку книг и обработку ошибок файлов.

Источник задания: [Lab6.md](https://github.com/Natali2531/Study/blob/main/Лабораторные%20работы%20по%20ООП/Lab6.md), просмотрен 30.10.2026. Реализована обязательная часть раздела 3. Индивидуальные варианты раздела 4 не выполнялись.

---

### 3. Краткое теоретическое введение

Исключение — объект, описывающий ситуацию, которая нарушает обычное выполнение программы. Все исключения относятся к иерархии `Throwable`. Проверяемые (checked) исключения требуют обработки либо объявления через `throws`; непроверяемые (unchecked) — `RuntimeException`, `Error` и их подклассы — такого требования не имеют. Оба вида возникают во время выполнения. `throw` непосредственно выбрасывает объект, а `catch` обрабатывает подходящий тип. Цепочка `cause` позволяет сохранить техническую причину при создании более понятного исключения.

`finally` предназначен для завершающих действий при выходе из `try`, включая выход при исключении; он не гарантирован при остановке JVM или процесса. Для освобождения файловых ресурсов применяется `try-with-resources`: он вызывает `close()` у объектов `AutoCloseable`. При одновременной ошибке в теле блока и при закрытии ошибка закрытия сохраняется как suppressed-исключение, не скрывая основную. См. [Oracle: Exceptions](https://docs.oracle.com/javase/tutorial/essential/exceptions/).

Логирование сохраняет события для последующей диагностики. SLF4J предоставляет API, а Logback реализует вывод через appenders — компоненты записи в консоль и файлы. Уровни от подробных к серьёзным: TRACE, DEBUG, INFO, WARN, ERROR. Порог INFO скрывает TRACE и DEBUG. Передача исключения последним аргументом logger сохраняет stack trace и цепочку причин. Подробное учебное объяснение с примерами находится в [docs/THEORY.md](docs/THEORY.md).

---

### 4. Листинги программного кода с пояснениями

Проект сохраняет плоскую структуру `src/` и `test/`. Исключения вынесены в пакет `exceptions` по п. 5.1 методички; конфигурация находится в `src/main/resources/logback.xml` и явно подключена в Maven.

Сохранён контракт репозитория №5: `save(T)` возвращает ключ, ключ принадлежит хранилищу, `findAll()` возвращает отдельный список, повторное сохранение создаёт новую запись. `findById()` и `delete()` теперь объявляют `throws BookNotFoundException`. Возвращаемый тип `delete()` сохранён: успешное удаление даёт `true`, отсутствие книги выражается исключением. Обобщённый `Repository<T>` используется с `Book`; предметное имя исключения задано методичкой. Дополнительные интерфейсы сущностей не вводились.

В обязательной части у книги нужны только `title`, `author`, `year`. Поля ISBN и издательство, а также уникальность ISBN относятся к индивидуальному варианту и не добавлены.

#### 4.1. Checked-исключение

Наследует Exception, хранит сообщение и при необходимости исходную причину.

Исходный файл: [src/exceptions/BookNotFoundException.java](src/exceptions/BookNotFoundException.java).

```java
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
```

#### 4.2. Unchecked-исключение

Наследует RuntimeException; обработка компилятором не навязывается.

Исходный файл: [src/exceptions/InvalidBookDataException.java](src/exceptions/InvalidBookDataException.java).

```java
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
```

#### 4.3. Класс Book

Конструктор применяет те же проверки, что и сеттеры. Проверка предшествует присваиванию, поэтому отклонённое изменение сохраняет прежнее поле.

Исходный файл: [src/Book.java](src/Book.java).

```java
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
```

#### 4.4. Класс Repository

LinkedHashMap сохраняет порядок вставки. INFO фиксирует успех, WARN — отсутствие книги, DEBUG — размер хранилища.

Исходный файл: [src/Repository.java](src/Repository.java).

```java
import exceptions.BookNotFoundException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище в памяти с собственными последовательными ключами, без потокобезопасности.
 * В этой работе используется как Repository<Book>; исключение поиска предметное.
 * @param <T> тип сохраняемых объектов
 */
public final class Repository<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Repository.class);
    private final Map<Long, T> storage = new LinkedHashMap<>();
    private long nextId = 1;

    /** Создаёт пустое хранилище. */
    public Repository() { }

    /**
     * Добавляет объект под новым ключом, даже если такой объект уже сохранён.
     * @param entity ненулевой объект
     * @return назначенный ключ
     * @throws NullPointerException если объект равен null
     * @throws ArithmeticException если диапазон счётчика ключей исчерпан
     */
    public long save(T entity) {
        Objects.requireNonNull(entity, "Объект репозитория не должен быть null");
        long id = nextId;
        nextId = Math.incrementExact(nextId);
        storage.put(id, entity);
        LOGGER.info("Объект успешно сохранён: id={}, {}", id, entity);
        return id;
    }

    /**
     * Находит объект по ключу хранилища.
     * @param id ключ
     * @return найденный объект
     * @throws BookNotFoundException если ключ отсутствует
     */
    public T findById(long id) throws BookNotFoundException {
        T entity = storage.get(id);
        if (entity == null) {
            LOGGER.warn("Попытка получить книгу с id {}", id);
            throw new BookNotFoundException("Книга с id " + id + " не найдена");
        }
        return entity;
    }

    /**
     * Удаляет объект по ключу.
     * @param id ключ
     * @return true после успешного удаления
     * @throws BookNotFoundException если ключ отсутствует
     */
    public boolean delete(long id) throws BookNotFoundException {
        T removed = storage.remove(id);
        if (removed == null) {
            LOGGER.warn("Попытка удалить книгу с id {}", id);
            throw new BookNotFoundException("Книга с id " + id + " не найдена");
        }
        LOGGER.info("Объект успешно удалён: id={}, {}", id, removed);
        return true;
    }

    /**
     * Возвращает отдельный изменяемый список в порядке сохранения.
     * @return поверхностная копия значений; сами объекты не копируются
     */
    public List<T> findAll() {
        LOGGER.debug("Получение всех объектов: количество={}", storage.size());
        return new ArrayList<>(storage.values());
    }
}
```

#### 4.5. Класс Main

Демонстрируются успех и ошибки, finally, чтение строк с продолжением после ошибочной строки, cause для нечислового года. Учебный лог записывается отдельно от файлов Logback, чтобы не конфликтовать с его appender.

Исходный файл: [src/Main.java](src/Main.java).

```java
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
```

#### 4.6. Конфигурация Logback

Консоль и application.log получают события от INFO. errors.log получает только ERROR. Для общего лога настроена ротация по размеру 10 МБ и дате с историей за 7 периодов. Используется SizeAndTimeBasedRollingPolicy вместо устаревшего класса из примера методички.

Исходный файл: [src/main/resources/logback.xml](src/main/resources/logback.xml).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
    <property name="PATTERN" value="%d{yyyy-MM-dd HH:mm:ss} %-5level %logger{36} - %msg%n"/>
    <appender name="CONSOLE" class="ch.qos.logback.core.ConsoleAppender">
        <encoder><charset>UTF-8</charset><pattern>${PATTERN}</pattern></encoder>
    </appender>
    <appender name="FILE" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>logs/application.log</file>
        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">
            <fileNamePattern>logs/application.%d{yyyy-MM-dd}.%i.log</fileNamePattern>
            <maxFileSize>10MB</maxFileSize>
            <maxHistory>7</maxHistory>
        </rollingPolicy>
        <encoder><charset>UTF-8</charset><pattern>${PATTERN}</pattern></encoder>
    </appender>
    <appender name="ERROR_FILE" class="ch.qos.logback.core.FileAppender">
        <file>logs/errors.log</file>
        <filter class="ch.qos.logback.classic.filter.LevelFilter">
            <level>ERROR</level><onMatch>ACCEPT</onMatch><onMismatch>DENY</onMismatch>
        </filter>
        <encoder><charset>UTF-8</charset><pattern>${PATTERN}</pattern></encoder>
    </appender>
    <root level="${log.level:-INFO}">
        <appender-ref ref="CONSOLE"/>
        <appender-ref ref="FILE"/>
        <appender-ref ref="ERROR_FILE"/>
    </root>
</configuration>
```

#### 4.7. Файл сборки Maven

Задана целевая Java 17. SLF4J 2.0.18 и Logback 1.6.6 подключены как зависимости. Shade включает библиотеки и ServiceLoader-регистрацию провайдера в запускаемый JAR. Версии логирования сверены с официальной страницей Logback; конфигурация проверена запуском.

Исходный файл: [pom.xml](pom.xml).

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.university</groupId>
    <artifactId>lab06-exceptions-logging</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.release>17</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.11.4</junit.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.slf4j</groupId>
            <artifactId>slf4j-api</artifactId>
            <version>2.0.18</version>
        </dependency>
        <dependency>
            <groupId>ch.qos.logback</groupId>
            <artifactId>logback-classic</artifactId>
            <version>1.6.6</version>
        </dependency>
        <dependency>
            <groupId>org.junit.jupiter</groupId>
            <artifactId>junit-jupiter</artifactId>
            <version>${junit.version}</version>
            <scope>test</scope>
        </dependency>
    </dependencies>

    <build>
        <sourceDirectory>src</sourceDirectory>
        <testSourceDirectory>test</testSourceDirectory>
        <resources>
            <resource><directory>src/main/resources</directory></resource>
        </resources>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-shade-plugin</artifactId>
                <version>3.6.0</version>
                <executions>
                    <execution>
                        <phase>package</phase>
                        <goals><goal>shade</goal></goals>
                        <configuration>
                            <createDependencyReducedPom>false</createDependencyReducedPom>
                            <transformers>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ServicesResourceTransformer"/>
                                <transformer implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
                                    <mainClass>Main</mainClass>
                                </transformer>
                            </transformers>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.13.0</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-surefire-plugin</artifactId>
                <version>3.5.2</version>
            </plugin>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.4.2</version>
                <configuration>
                    <archive>
                        <manifest>
                            <mainClass>Main</mainClass>
                        </manifest>
                    </archive>
                </configuration>
            </plugin>
        </plugins>
    </build>
</project>
```

Тесты: [BookTest.java](test/BookTest.java), [RepositoryTest.java](test/RepositoryTest.java), [FileLoadingTest.java](test/FileLoadingTest.java). Проверяются границы года, null/пустые строки, сохранность полей при отказе, операции хранилища, независимость списка, ошибочные строки, отсутствующий файл и сохранение cause.

---

### 5. Протокол работы программы

Команды из корня репозитория:

```bash
cd lab06-exceptions-logging
mvn clean verify
java -jar target/lab06-exceptions-logging-1.0.0.jar
```

Только тестирование: `mvn test`. Для подробных сообщений:

```bash
java -Dlog.level=DEBUG -jar target/lab06-exceptions-logging-1.0.0.jar
```

Другой входной файл передаётся первым аргументом после имени JAR. Пути `data/` и `logs/` разрешаются относительно рабочей папки, поэтому в IntelliJ IDEA рабочая папка Run Configuration должна указывать на корень этой лабораторной. Maven-проект открывается через `pom.xml`.

**Проверка агентом 30.10.2026:** `mvn verify` завершился BUILD SUCCESS, 16 тестовых случаев, 0 failures, 0 errors, 0 skipped. Использовался OpenJDK 25.0.4.1 с `--release 17`; отдельный запуск на JVM 17 не выполнялся. Собранный JAR фактически запущен. Итог: загружены 3 книги, отклонены 3 строки, обработано отсутствие файла; финальный размер репозитория — 3.

Полный фактический текстовый протокол агента: [docs/run-output.txt](docs/run-output.txt). Даты, номера строк stack trace и текущий год могут измениться при другом запуске; `mvn clean` очищает `target/`, но не файлы в `logs/`. Logback дописывает application.log и errors.log, а учебный file-demo.log перезаписывается при каждой демонстрации.

Фрагмент фактического вывода:

```text
BookNotFoundException: Книга с id 999 не найдена
finally: демонстрация поиска завершена
Удаление: Книга с id 999 не найдена
Год после отклонённого изменения: 2000
```

В логах зафиксировано `загружено=3, отклонено=3`. Завершающий вывод:

```text
Прочитано из logs/file-demo.log: INFO: учебная запись в файл
Книг в репозитории: 3
```

**Место для пользовательского протокола:** после собственного запуска добавить скриншот `docs/images/run.png` и включить изображение следующей Markdown-строкой:

```markdown
![Протокол работы программы](docs/images/run.png)
```

Пользовательский скриншот пока не предоставлен. Текстовый вывод выше получен запуском агента.

---

### 6. Выписка из журнала применения ИИ

Инструмент: Codex. Журнал оформлен как **реконструированный сценарий работы**, а не дословная переписка: [docs/prompts/README.md](docs/prompts/README.md).

Примеры этапов: сверка обязательной части, создание двух исключений, доработка Repository, валидация Book, настоящая загрузка файла, конфигурация логирования и проверка результата. При анализе исправлены недостатки примеров методички: отсутствие `throws` для checked-исключения в некоторых фрагментах и имитация чтения файла вместо настоящей реализации. В логировании использована действующая политика ротации.

---

### 7. Ответы на контрольные вопросы

1. **Что такое иерархия исключений в Java? Назовите основные классы.**  
   Это наследование классов от Throwable. Основные ветви — Error и Exception. RuntimeException является подклассом Exception. Error, RuntimeException и их подклассы — unchecked; остальные подклассы Throwable — checked. Примеры: IOException — checked, NumberFormatException — unchecked.

2. **В чём разница между checked и unchecked исключениями?**  
   Для checked компилятор требует catch либо throws. Для unchecked такого требования нет. Оба вида возникают во время выполнения; разница в обязательности объявления и обработки.

3. **Что такое try-with-resources и как он работает?**  
   Это try с объявленными ресурсами AutoCloseable. После работы автоматически вызывается close(), в том числе при исключении. Несколько ресурсов закрываются в обратном порядке. Ошибки закрытия при уже возникшей основной ошибке сохраняются как suppressed.

4. **В чём отличие между throw и throws?**  
   throw выбрасывает конкретный объект исключения в теле метода. throws перечисляет возможные исключения в объявлении метода и передаёт обязанность обработки вызывающему коду.

5. **Как создать собственное checked-исключение?**  
   Унаследовать класс от Exception, не от RuntimeException. Конструкторы передают message и cause родительскому конструктору. Пример — BookNotFoundException; методы поиска и удаления объявляют его в throws.

6. **Как создать собственное unchecked-исключение?**  
   Унаследовать класс от RuntimeException. Пример — InvalidBookDataException. Оно выбрасывается при нарушении правил данных книги; catch необязателен для компилятора, но используется в демонстрации для продолжения работы.

7. **Что такое SLF4J и Logback?**  
   SLF4J — API логирования, через который приложение отправляет сообщения. Logback — реализация, форматирующая и записывающая эти сообщения в настроенные направления вывода.

8. **Перечислите уровни логирования и опишите, когда какой использовать.**  
   TRACE — самые подробные шаги; DEBUG — диагностические детали; INFO — основные штатные события; WARN — проблемы без остановки текущего приложения; ERROR — ошибки операций. В работе уровень ERROR для неверных данных установлен требованиями методички. Порог INFO включает INFO, WARN, ERROR.

9. **Почему важно логировать операции в приложении?**  
   Журнал показывает последовательность событий, результаты операций, контекст и причины ошибок. Это помогает диагностировать проблему после её возникновения. Вызов logger сам по себе не обрабатывает исключение и не останавливает выполнение.

10. **Что такое цепочка причин (cause) в исключениях?**  
    Это связь нового исключения с исходным через конструктор (message, cause). Например, InvalidBookDataException сохраняет NumberFormatException при преобразовании года. getCause() возвращает исходную причину; в stack trace она выводится после Caused by.

11. **В чём отличие finally от try-with-resources?**  
    finally выполняет произвольное завершающее действие, но сам не закрывает ресурсы. Try-with-resources автоматически закрывает AutoCloseable и корректно сохраняет дополнительные ошибки закрытия. Для файлов предпочтителен try-with-resources. При остановке JVM выполнение finally не гарантируется.

12. **Как настроить логирование в файл?**  
    Добавить FileAppender или RollingFileAppender в logback.xml, указать путь file, encoder с форматом и кодировкой, затем подключить appender к root через appender-ref. Для ротации указывают rollingPolicy; для файла только с ERROR применяется LevelFilter. Конфигурация должна находиться в classpath.

---

### 8. Выводы

Реализована обязательная часть лабораторной: собственные checked/unchecked исключения, валидация книги, доработка обобщённого репозитория и логирование. Ошибка отдельной строки не мешает обработке следующих строк; неудачная проверка сеттера не изменяет поле объекта. Причина нечислового года сохраняется в cause. Файлы действительно читаются и записываются с автоматическим закрытием ресурсов.

Сборка, 16 тестовых случаев и запуск JAR выполнены успешно. Журналы фиксируют INFO, WARN и ERROR; DEBUG доступен при изменении порога. Для завершения пользовательского протокола предусмотрено место под собственный скриншот.

Источники: [методичка Lab6](https://github.com/Natali2531/Study/blob/main/Лабораторные%20работы%20по%20ООП/Lab6.md), [Oracle: Exceptions](https://docs.oracle.com/javase/tutorial/essential/exceptions/), [SLF4J Manual](https://www.slf4j.org/manual.html), [Logback Download](https://logback.qos.ch/download.html).
