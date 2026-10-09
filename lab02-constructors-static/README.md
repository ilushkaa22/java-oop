# Отчёт по лабораторной работе №2

### 1. Титульный лист

**Лабораторная работа №2**  
по дисциплине: «Объектно-ориентированное программирование»  
на тему: «Конструкторы, перегрузка, static»

**Выполнил:**  
Студент группы ИИ21/1  
Саврико Илья

---

### 2. Цель и задачи работы

**Цель работы:** освоить разные способы инициализации объектов, перегрузку и применение членов класса с модификатором `static`.

**Задачи работы:**

1. Развить класс `Book` из первой лабораторной, добавив четыре конструктора и делегирование через `this(...)`.
2. Подсчитывать созданные книги в общем поле `counter` и предоставлять метод `getCounter()`.
3. Реализовать фабрику `createBook(title, author, year)` с автоматическим ID.
4. Добавить выбор формата через `getDescription(boolean shortFormat)`.
5. Продемонстрировать поведение и проверить его тестами.

Источник задания: [актуальная методичка Lab2.md](https://github.com/Natali2531/Study/blob/main/Лабораторные%20работы%20по%20ООП/Lab2.md), проверена 09.10.2026. Выполнена обязательная часть; индивидуальный вариант не запрашивался.

---

### 3. Краткое теоретическое введение

Конструктор инициализирует новый объект. Перегрузка (overloading) позволяет определить несколько конструкторов или методов с одинаковым именем и разными списками параметров. Компилятор выбирает подходящий вариант по аргументам. В реализации для Java 17 вызов `this(...)` стоит первым в теле конструктора: он передаёт инициализацию основному конструктору, устраняя повторение проверок.

Нестатические поля описывают состояние отдельной книги, а статическое поле `counter` общее для всех экземпляров этого класса. Оно увеличивается после успешной проверки данных: исключение при создании книги не должно увеличивать число созданных объектов. Статический метод можно вызвать через имя класса, например `Book.getCounter()`. У такого метода нет текущего объекта `this`, поэтому для доступа к нестатическому состоянию нужна явная ссылка на объект.

Статический фабричный метод (static factory method) возвращает объект и даёт операции создания осмысленное имя. В этом проекте фабрика назначает ID по порядковому номеру создания книги. Перегруженный метод описания выбирает между полным форматом с автором и коротким форматом без автора; полная ветвь использует существующий метод, сохраняя единое правило форматирования.

---

### 4. Структура проекта

```text
lab02-constructors-static/
├── data/README.md
├── docs/
│   ├── images/README.md
│   └── prompts/README.md
├── src/
│   ├── Book.java
│   └── Main.java
├── test/BookTest.java
├── pom.xml
└── README.md
```

Проект основан на исходных файлах лабораторной №1 и оформлен отдельно по правилам AGENTS.md. Внешние входные файлы не нужны: демонстрационные данные находятся в `Main`.

---

### 5. Листинги программного кода с пояснениями

#### 5.1. Класс `Book`

Все дополнительные конструкторы вызывают основной. Проверки названия, автора и года сохранены из первой лабораторной. Допустимый ID теперь неотрицательный: `0` означает незаданный идентификатор, как требует обязательная часть второй работы. Счётчик изменяется только в конце основного конструктора.

В задании конструкторы без ID должны сохранять `0`, а пример ИИ-промпта предлагает автоматический ID в основном конструкторе. Выбрано требование основного задания: автоматическое назначение выполняется только фабрикой. Поэтому после четырёх конструкторных вызовов фабрика создаёт пятую книгу с ID `5`.

```java
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
```

Счётчик обозначает число успешных созданий за время работы JVM, а не количество книг, находящихся в памяти в данный момент. Удаление ссылок не уменьшает его. Явные ID и изменения через `setId()` не проверяются на уникальность: ручной ID может совпасть с фабричным. Последовательные фабричные вызовы в этом однопоточном приложении получают возрастающие ID; после создания книги другим способом возможен пропуск номера. Сохранение ID между запусками и работа из нескольких потоков не входят в модель лабораторной.

#### 5.2. Класс `Main`

Показаны все четыре конструктора, фабрика, оба формата описания и общий счётчик.

```java
/** Демонстрирует конструкторы, фабричный метод и static в лабораторной №2. */
public final class Main {
    private Main() {
    }

    /**
     * Создаёт книги всеми предусмотренными способами и выводит статистику.
     * @param args аргументы командной строки, не используются
     */
    public static void main(String[] args) {
        Book defaultBook = new Book();
        Book fullBook = new Book(100, "Война и мир", "Толстой Л.Н.", 1869);
        Book bookWithoutId = new Book("Преступление и наказание", "Достоевский Ф.М.", 1866);
        Book bookWithoutYear = new Book("Евгений Онегин", "Пушкин А.С.");
        Book factoryBook = Book.createBook("Мастер и Маргарита", "Булгаков М.А.", 1967);

        System.out.println("=== Создание книг ===");
        printBook("Без параметров", defaultBook);
        printBook("Со всеми параметрами", fullBook);
        printBook("Без id", bookWithoutId);
        printBook("Только название и автор", bookWithoutYear);
        printBook("Через фабричный метод", factoryBook);
        System.out.println("Краткое описание: " + bookWithoutId.getDescription(true));
        System.out.println("Полное описание: " + bookWithoutId.getDescription(false));
        System.out.println();
        System.out.println("=== Статистика ===");
        System.out.println("Всего создано книг: " + Book.getCounter());
    }

    private static void printBook(String label, Book book) {
        System.out.printf("%s: id=%d, %s%n", label, book.getId(), book.getDescription());
    }
}
```

#### 5.3. Модульные тесты

Полный код: [test/BookTest.java](test/BookTest.java). Проверяются поля, сеттеры, валидация, значения по умолчанию, подсчёт всех способов создания, последовательность ID фабрики, оба формата и отсутствие изменений счётчика после исключения. Тесты сравнивают изменение счётчика с исходным значением, поэтому не зависят от порядка выполнения.

```java
import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookTest {
    @Test
    void constructorAndGettersStoreValidValues() {
        Book book = new Book(1, "Война и мир", "Толстой Л.Н.", 1869);

        assertEquals(1, book.getId());
        assertEquals("Война и мир", book.getTitle());
        assertEquals("Толстой Л.Н.", book.getAuthor());
        assertEquals(1869, book.getYear());
    }

    @Test
    void settersUpdateBook() {
        Book book = new Book(1, "Название", "Автор", 2000);

        book.setId(2);
        book.setTitle("Новое название");
        book.setAuthor("Новый автор");
        book.setYear(2020);

        assertEquals(2, book.getId());
        assertEquals("Новое название", book.getTitle());
        assertEquals("Новый автор", book.getAuthor());
        assertEquals(2020, book.getYear());
    }

    @Test
    void descriptionHasRequiredFormat() {
        Book book = new Book(1, "Война и мир", "Толстой Л.Н.", 1869);

        String expected = "\"Война и мир\" — Толстой Л.Н. (1869)";
        assertEquals(expected, book.getDescription());
        assertEquals(expected, book.toString());
    }

    @Test
    void idMustNotBeNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(-1, "Название", "Автор", 2000));
    }

    @Test
    void titleMustNotBeNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, null, "Автор", 2000));
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "   ", "Автор", 2000));
    }

    @Test
    void authorMustNotBeNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "Название", null, 2000));
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "Название", "   ", 2000));
    }

    @Test
    void yearMustBeInAllowedRange() {
        int nextYear = Year.now().getValue() + 1;

        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "Название", "Автор", -1));
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "Название", "Автор", nextYear));
    }

    @Test
    void defaultConstructorUsesRequiredValues() {
        Book book = new Book();
        assertEquals(0, book.getId());
        assertEquals("Без названия", book.getTitle());
        assertEquals("Неизвестен", book.getAuthor());
        assertEquals(0, book.getYear());
    }

    @Test
    void constructorsWithoutIdUseZero() {
        Book withYear = new Book("Название", "Автор", 2000);
        Book withoutYear = new Book("Название", "Автор");
        assertEquals(0, withYear.getId());
        assertEquals(2000, withYear.getYear());
        assertEquals(0, withoutYear.getId());
        assertEquals("Название", withoutYear.getTitle());
        assertEquals("Автор", withoutYear.getAuthor());
        assertEquals(0, withoutYear.getYear());
    }

    @Test
    void counterCountsEveryCreationExactlyOnce() {
        int before = Book.getCounter();
        new Book();
        new Book(100, "Название", "Автор", 2000);
        new Book("Название", "Автор", 2000);
        new Book("Название", "Автор");
        Book.createBook("Название", "Автор", 2000);
        assertEquals(before + 5, Book.getCounter());
    }

    @Test
    void factoryUsesCreationNumberAndPreservesValues() {
        int before = Book.getCounter();
        Book first = Book.createBook("Название", "Автор", 2000);
        Book second = Book.createBook("Другая книга", "Другой автор", 2001);
        assertEquals((long) before + 1, first.getId());
        assertEquals(first.getId() + 1, second.getId());
        assertEquals("Название", first.getTitle());
        assertEquals("Автор", first.getAuthor());
        assertEquals(2000, first.getYear());
        assertEquals(before + 2, Book.getCounter());
    }

    @Test
    void failedCreationDoesNotIncreaseCounterOrSkipFactoryId() {
        int before = Book.getCounter();
        assertThrows(IllegalArgumentException.class, () -> new Book(-1, "Книга", "Автор", 2000));
        assertThrows(IllegalArgumentException.class, () -> new Book(" ", "Автор"));
        assertThrows(IllegalArgumentException.class, () -> new Book("Книга", null, 2000));
        assertThrows(IllegalArgumentException.class, () -> Book.createBook("Книга", "Автор", -1));
        assertEquals(before, Book.getCounter());
        assertEquals((long) before + 1, Book.createBook("Книга", "Автор", 2000).getId());
    }

    @Test
    void descriptionSupportsBothFormats() {
        Book book = new Book("Война и мир", "Толстой Л.Н.", 1869);
        assertEquals("Война и мир (1869)", book.getDescription(true));
        assertEquals(book.getDescription(), book.getDescription(false));
    }

    @Test
    void settersDoNotChangeCounterAndAllowUnassignedId() {
        Book book = new Book();
        int before = Book.getCounter();
        book.setId(10);
        book.setId(0);
        book.setTitle("Книга");
        book.setAuthor("Автор");
        book.setYear(2000);
        assertThrows(IllegalArgumentException.class, () -> book.setId(-1));
        assertEquals(0, book.getId());
        assertEquals(before, Book.getCounter());
    }
}
```

#### 5.4. Настройка сборки

Maven использует плоские каталоги `src/` и `test/`, JUnit 5 и `release=17`. В манифест JAR добавлен класс `Main`, поэтому приложение запускается через `java -jar`.

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.university</groupId>
    <artifactId>lab02-constructors-static</artifactId>
    <version>1.0.0</version>

    <properties>
        <maven.compiler.release>17</maven.compiler.release>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <junit.version>5.11.4</junit.version>
    </properties>

    <dependencies>
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
        <plugins>
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

---

### 6. Протокол работы программы

Фактический текстовый вывод проверочного запуска исполняемого JAR 09.10.2026 (это запуск при подготовке проекта, пользовательский скриншот добавляется отдельно):

```text
=== Создание книг ===
Без параметров: id=0, "Без названия" — Неизвестен (0)
Со всеми параметрами: id=100, "Война и мир" — Толстой Л.Н. (1869)
Без id: id=0, "Преступление и наказание" — Достоевский Ф.М. (1866)
Только название и автор: id=0, "Евгений Онегин" — Пушкин А.С. (0)
Через фабричный метод: id=5, "Мастер и Маргарита" — Булгаков М.А. (1967)
Краткое описание: Преступление и наказание (1866)
Полное описание: "Преступление и наказание" — Достоевский Ф.М. (1866)

=== Статистика ===
Всего создано книг: 5
```

**Место для пользовательского скриншота:** сохраните снимок своего запуска как `docs/images/program-output.png` и замените этот абзац вставкой:

```markdown
![Протокол работы программы](docs/images/program-output.png)
```

---

### 7. Выписка из журнала применения ИИ

Использован OpenAI Codex. Указанные в методичке YandexGPT и GigaChat в этой работе не применялись. [Полный журнал](docs/prompts/README.md) содержит десять этапов и оформлен как реконструированный сценарий, а не дословная переписка.

Пример промпта сценария: «Проверь согласованность счётчика и фабрики: объект с ошибочными данными не должен менять статистику, а делегирование конструкторов не должно увеличивать её повторно». Результат: инкремент перенесён в конец основного конструктора, фабрика использует его без дополнительного увеличения. Критический анализ: проверены исключения и изменение счётчика на одну книгу; отдельно отмечено, что ручные ID не гарантируют глобальную уникальность.

---

### 8. Ответы на контрольные вопросы

1. **Перегрузка конструкторов.** Несколько конструкторов одного класса имеют разные списки параметров. Это даёт несколько способов инициализировать объект, например `new Book()` и `new Book("Книга", "Автор")`.

2. **Вызов другого конструктора.** Используется `this(аргументы)`. В коде для Java 17 этот вызов должен быть первой инструкцией конструктора. Циклическое делегирование запрещено.

3. **Статическое и нестатическое поле.** Поле с `static` относится к классу и общее для его экземпляров, например `counter`. Поле без `static`, например `title`, хранит своё значение в каждом объекте.

4. **Статический метод и доступ к полям объекта.** Метод с `static` вызывается без создания экземпляра и не имеет `this`. Он не может напрямую обращаться к нестатическому полю, но может работать с ним через явную ссылку, например `book.getTitle()`.

5. **Фабричный метод и его преимущества.** Статическая фабрика — именованный метод, возвращающий объект. Имя объясняет назначение операции; метод может выбрать реализацию возвращаемого типа, вернуть подкласс или повторно использовать готовый экземпляр. Наша фабрика всегда создаёт новую книгу и назначает ID.

6. **Перегрузка и переопределение.** Перегрузка меняет список параметров; вариант выбирается при компиляции. Одного изменения возвращаемого типа недостаточно. Переопределение (overriding) задаёт в подклассе новую реализацию унаследованного метода с той же сигнатурой и совместимым возвращаемым типом; для методов экземпляра реализация выбирается во время выполнения. Статические методы скрываются, а не переопределяются. В `Book` перегружен `getDescription`, а `toString` переопределён из `Object`.

7. **Обращение к статическому методу.** Через имя класса: `Book.getCounter()` или `Book.createBook("Книга", "Автор", 2000)`. Вызов через экземпляр допускается синтаксисом Java, но скрывает принадлежность метода классу.

8. **Назначение `this`.** Обозначает текущий объект, помогает отличить поле от параметра (`this.id = id`), обратиться к членам объекта или передать текущий объект. Форма `this(...)` вызывает другой конструктор того же класса. В статическом контексте `this` недоступно.

---

### 9. Выводы

Класс `Book` расширен без дублирования инициализации: четыре конструктора используют единый основной конструктор. Статическая фабрика назначает ID, общий счётчик учитывает успешное создание, а перегрузка описания предоставляет два формата. Сохранены инкапсуляция, проверка входных значений и документация публичного API. Тесты проверяют успешные и ошибочные сценарии.

---

### 10. Сборка, тестирование и запуск

Требуются JDK 17+ и Maven. Из каталога `lab02-constructors-static`:

```bash
mvn clean verify
java -jar target/lab02-constructors-static-1.0.0.jar
```

`verify` выполняет компиляцию, тесты и сборку JAR. Для запуска тестов отдельно: `mvn test`.

В текущем окружении `mvn` отсутствует в PATH. Можно использовать Maven, поставляемый с IntelliJ IDEA:

```bash
"/home/ilyshka/Загрузки/idea-2026.2.2/idea-IU-262.10315.125/plugins/maven-plugin/lib/maven3/bin/mvn" clean verify
```

Проверка выполнена 09.10.2026 на OpenJDK 25.0.4.1 и Maven 3.9.16: `clean verify` завершился с `BUILD SUCCESS`; 14 тестов прошли, ошибок и пропусков нет. JAR запущен командой выше, его фактический вывод приведён в разделе 6. `javap -verbose` подтвердил major version 61 (байткод Java 17); запуск на самом JDK 17 отдельно не выполнялся.

Для проверки использована команда с отдельным кешем зависимостей:

```bash
"/home/ilyshka/Загрузки/idea-2026.2.2/idea-IU-262.10315.125/plugins/maven-plugin/lib/maven3/bin/mvn" -Dmaven.repo.local=/tmp/lab02-maven-repository clean verify
```

Первоначальная offline-проверка не завершилась из-за отсутствующих зависимостей плагинов. После их загрузки полная сборка прошла успешно. Временный кеш `/tmp` не является частью проекта: обычная команда Maven использует стандартный локальный репозиторий и при необходимости загружает зависимости.
