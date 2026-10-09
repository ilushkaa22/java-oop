# Отчёт по лабораторной работе №4

### 1. Титульный лист

**Лабораторная работа №4**  
по дисциплине: «Объектно-ориентированное программирование»  
на тему: «Коллекции: List, Set, Map»

**Выполнил:**  
Студент группы ИИ21/1  
Саврико Илья

---

### 2. Цель и задачи работы

**Цель работы:** освоить коллекции Java, сортировку и фильтрацию книг, Stream API и измерение времени поиска.

**Задачи работы:**

1. Обработать список из десяти книг: отсортировать по году через `Collections.sort` и `Comparator`, затем по автору.
2. Получить книги строго после 2000 года.
3. Проверить отклонение отдельного равного объекта в `HashSet` с помощью `equals()` и `hashCode()`.
4. Сгруппировать книги по автору в `Map<String, List<Book>>`.
5. Применить `filter()`, `map()`, `sorted()` и `collect(Collectors.groupingBy(...))`, подсчитать книги по авторам.
6. Сравнить поиск в `ArrayList` и `HashSet` на 100 000 элементах.

Источник: [Lab4.md](https://github.com/Natali2531/Study/blob/main/Лабораторные%20работы%20по%20ООП/Lab4.md), просмотрен 09.10.2026. Выполнена обязательная часть раздела «Задание на паре». Индивидуальный вариант не запрошен и не выполнялся.

Проект независим от сборки лабораторных №1–3: для запуска не нужны их исходники или JAR. Обязательная часть не требует иерархии сотрудников и книг из №3. Ссылка методички на класс лабораторной №1 находится в разделе индивидуальных заданий. Сохранена общая предметная область и поля `Book`: `id`, `title`, `author`, `year`. Здесь класс упрощён до неизменяемого объекта без сеттеров, счётчика и фабрики прошлых работ: это позволяет безопасно использовать все поля в хешировании.

---

### 3. Краткое теоретическое введение

Java Collections Framework — набор интерфейсов и реализаций для хранения групп объектов. `List` сохраняет порядок и допускает повторы; `Set` задаёт уникальность; `Map` связывает ключ с значением и не наследует `Collection`. `ArrayList` хранит элементы в массиве и обеспечивает доступ по индексу за O(1), а поиск через `contains()` — за O(n). `LinkedList` представляет двусвязный список: вставка рядом с уже известной позицией итератора не требует сдвига, но поиск этой позиции занимает O(n). `HashSet` использует хеширование, `TreeSet` — упорядоченное дерево; соответствующие карты — `HashMap` и `TreeMap`.

Контракт `equals()` определяет логическое равенство объектов, а `hashCode()` — хеш для поиска в хеш-таблице. Равные объекты обязаны иметь одинаковые хеши; одинаковые хеши не гарантируют равенства. В этой работе книга равна другой только при совпадении всех четырёх полей, включая ID. Разные ID означают разные записи, даже при совпадении названия, автора и года. Поля `private final` и отсутствие сеттеров предотвращают изменение хеша после помещения книги в `HashSet`. Для `TreeSet` уникальность определяется сравнением, поэтому его правило сравнения также должно согласовываться с выбранным равенством.

Stream API — интерфейс последовательной обработки элементов. Intermediate operations (промежуточные операции) `filter`, `map`, `sorted` формируют цепочку преобразований; terminal operations (терминальные операции) `collect`, `count`, `forEach` запускают обработку. `Comparator` задаёт внешний порядок сравнения; `thenComparing` добавляет следующие поля при равенстве предыдущих. Для измерения длительности используется разность значений `System.nanoTime()`: создание данных, прогрев и печать вынесены за измеряемый участок. Учебный замер показывает конкретные результаты, но не заменяет строгий benchmark (измерение производительности) с JMH.

---

### 4. Структура проекта и соответствие заданию

```text
lab04-collections/
├── src/
│   ├── Book.java
│   ├── SampleBooks.java
│   ├── BookCollections.java
│   ├── SearchBenchmark.java
│   └── Main.java
├── test/
│   ├── BookTest.java
│   └── BookCollectionsTest.java
├── data/README.md
├── docs/
│   ├── images/README.md
│   ├── prompts/README.md
│   └── verification-output.txt
├── pom.xml
└── README.md
```

| Требование | Реализация |
|---|---|
| Десять книг в `List<Book>` | `SampleBooks.create()` возвращает новый `ArrayList` |
| Сортировка по году и автору | `sortByYear()` и `sortByAuthor()`, `Collections.sort` с `Comparator` |
| Книги после 2000 года | `publishedAfter(books, 2000)`: строго `year > 2000` |
| Проверка дубликата в `Set<Book>` | `Main.demonstrateSet()`, отдельная копия первой книги |
| Группировка в `Map<String, List<Book>>` | `groupByAuthor()`, `groupingBy` с `TreeMap` |
| Все требуемые операции Stream API | `publishedAfter()`, `titles()`, `groupByAuthor()` |
| Агрегация без явного цикла | `countByAuthor()` с `Collectors.counting()` |
| Поиск на 100 000 элементах | `SearchBenchmark.run()`, `contains()` в обеих коллекциях |
| Проверка кода ИИ | JUnit-тесты равенства, хеша и поведения коллекций |

`TreeMap` использован для воспроизводимого порядка авторов при печати. Это сравнение строк средствами Java, а не локализованная словарная сортировка. Внутри группы сохраняется исходный порядок книг. При сортировке списков второе поле — название. Исходный список не изменяется. Внешние данные не требуются; состав учебного набора описан в [data/README.md](data/README.md).

---

### 5. Листинги программного кода с пояснениями

#### 5.1. `Book.java`

Неизменяемая книга. В `equals` и `hashCode` используется одинаковый набор полей. `final` запрещает подклассы, поэтому проверка точного класса не создаёт неоднозначности равенства между базовым объектом и подклассом.

```java
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
```

#### 5.2. `SampleBooks.java`

Новый список из десяти разных книг при каждом вызове. Три записи одного автора позволяют наглядно проверить группировку. Годы 1999 и 2000 проверяют исключение книг на границе фильтра.

```java
import java.util.ArrayList;
import java.util.List;

/** Воспроизводимые учебные данные без внешних файлов. */
public final class SampleBooks {
    private SampleBooks() { }

    /**
     * Создаёт демонстрационный набор книг.
     * @return новый изменяемый список из десяти разных книг
     */
    public static List<Book> create() {
        return new ArrayList<>(List.of(
                new Book(1, "Чистый код", "Роберт Мартин", 2008),
                new Book(2, "Эффективная Java", "Джошуа Блох", 2018),
                new Book(3, "Философия Java", "Брюс Эккель", 2006),
                new Book(4, "Чистая архитектура", "Роберт Мартин", 2017),
                new Book(5, "Java: полное руководство", "Герберт Шилдт", 2021),
                new Book(6, "Алгоритмы", "Роберт Седжвик", 2011),
                new Book(7, "Совершенный код", "Стив Макконнелл", 2004),
                new Book(8, "Программист-прагматик", "Эндрю Хант", 1999),
                new Book(9, "Объектно-ориентированное проектирование", "Гради Буч", 2000),
                new Book(10, "Чистый Agile", "Роберт Мартин", 2019)));
    }
}
```

#### 5.3. `BookCollections.java`

Сортировки создают копии; фильтрация и агрегация используют Stream API. `map` преобразует книги в названия. `groupingBy` создаёт карту групп, а `counting` подсчитывает элементы каждой группы.

```java
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Сортировка и обработка коллекций книг без изменения исходного списка. */
public final class BookCollections {
    private BookCollections() { }

    /**
     * Сортирует копию списка по году и названию.
     * @param books исходные книги
     * @return копия, отсортированная по году и названию
     */
    public static List<Book> sortByYear(List<Book> books) {
        List<Book> sorted = new ArrayList<>(books);
        Collections.sort(sorted, Comparator.comparingInt(Book::getYear)
                .thenComparing(Book::getTitle));
        return sorted;
    }

    /**
     * Сортирует копию списка по автору и названию.
     * @param books исходные книги
     * @return копия, отсортированная по автору и названию
     */
    public static List<Book> sortByAuthor(List<Book> books) {
        List<Book> sorted = new ArrayList<>(books);
        Collections.sort(sorted, Comparator.comparing(Book::getAuthor)
                .thenComparing(Book::getTitle));
        return sorted;
    }

    /**
     * Фильтрует книги и сортирует результат средствами Stream API.
     * @param books исходные книги
     * @param year нижняя граница, не включается в результат
     * @return книги строго после указанного года, по названию
     */
    public static List<Book> publishedAfter(List<Book> books, int year) {
        return books.stream()
                .filter(book -> book.getYear() > year)
                .sorted(Comparator.comparing(Book::getTitle))
                .collect(Collectors.toList());
    }

    /**
     * Получает названия с помощью map().
     * @param books исходные книги
     * @return названия книг с сохранением порядка
     */
    public static List<String> titles(List<Book> books) {
        return books.stream().map(Book::getTitle).collect(Collectors.toList());
    }

    /**
     * Группирует книги по автору.
     * @param books исходные книги
     * @return группы книг с ключами в порядке авторов
     */
    public static Map<String, List<Book>> groupByAuthor(List<Book> books) {
        return books.stream().collect(Collectors.groupingBy(
                Book::getAuthor, TreeMap::new, Collectors.toList()));
    }

    /**
     * Подсчитывает книги по автору.
     * @param books исходные книги
     * @return количество книг каждого автора
     */
    public static Map<String, Long> countByAuthor(List<Book> books) {
        return books.stream().collect(Collectors.groupingBy(
                Book::getAuthor, TreeMap::new, Collectors.counting()));
    }
}
```

#### 5.4. `SearchBenchmark.java`

Создаются две коллекции из одинаковых 100 000 объектов. Для успешного поиска берётся отдельный объект, равный последней книге списка. Для каждой коллекции и каждого случая выполняется прогрев из 20 поисков, затем 100 поисков в каждом из трёх раундов. Порядок коллекций чередуется. Время делится на число поисков; найденные элементы подсчитываются и проверяются.

```java
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/** Учебный замер contains; время создания коллекций не входит в измерение. */
public final class SearchBenchmark {
    private static final int ELEMENT_COUNT = 100_000;
    private static final int WARMUP_REPETITIONS = 20;
    private static final int MEASURE_REPETITIONS = 100;
    private static final int ROUNDS = 3;
    private static final double NANOS_PER_MICROSECOND = 1_000.0;

    private SearchBenchmark() { }

    /** Выполняет прогрев и три раунда поиска существующей и отсутствующей книги. */
    public static void run() {
        List<Book> list = createBooks();
        Set<Book> set = new HashSet<>(list);
        // Равный, но отдельно созданный объект: поиск проверяет equals, а не только ссылку.
        Book present = new Book(ELEMENT_COUNT - 1, "Книга " + (ELEMENT_COUNT - 1),
                "Учебный автор", 2020);
        Book absent = new Book(ELEMENT_COUNT, "Нет в коллекции", "Учебный автор", 2020);
        System.out.printf("%nПоиск: %,d элементов; %d повторений на случай; %d раунда.%n",
                ELEMENT_COUNT, MEASURE_REPETITIONS, ROUNDS);
        warmup(list, set, present, absent);
        for (int round = 1; round <= ROUNDS; round++) {
            System.out.println("Раунд " + round + ":");
            // Чередуем порядок коллекций, чтобы не всегда измерять список первым.
            if (round % 2 == 1) {
                measureCollection("ArrayList", list, present, absent);
                measureCollection("HashSet", set, present, absent);
            } else {
                measureCollection("HashSet", set, present, absent);
                measureCollection("ArrayList", list, present, absent);
            }
        }
        System.out.println("Времена зависят от JVM и нагрузки; это учебный замер, не JMH.");
    }

    private static List<Book> createBooks() {
        List<Book> books = new ArrayList<>(ELEMENT_COUNT);
        for (int i = 0; i < ELEMENT_COUNT; i++) {
            books.add(new Book(i, "Книга " + i, "Учебный автор", 2020));
        }
        return books;
    }

    private static void warmup(Collection<Book> list, Collection<Book> set,
                               Book present, Book absent) {
        for (Book probe : List.of(present, absent)) {
            search(list, probe, WARMUP_REPETITIONS);
            search(set, probe, WARMUP_REPETITIONS);
        }
    }

    private static void measureCollection(String name, Collection<Book> books,
                                          Book present, Book absent) {
        measure(name, "есть (конец списка)", books, present, MEASURE_REPETITIONS);
        measure(name, "нет", books, absent, 0);
    }

    private static void measure(String name, String scenario, Collection<Book> books,
                                Book probe, int expected) {
        long start = System.nanoTime();
        int found = search(books, probe, MEASURE_REPETITIONS);
        long elapsed = System.nanoTime() - start;
        if (found != expected) {
            throw new IllegalStateException("Некорректный результат поиска: " + found);
        }
        System.out.printf(Locale.ROOT, "  %s / %s: %.3f мкс/поиск; найдено %d/%d%n",
                name, scenario, elapsed / (NANOS_PER_MICROSECOND * MEASURE_REPETITIONS),
                found, MEASURE_REPETITIONS);
    }

    private static int search(Collection<Book> books, Book probe, int repetitions) {
        int found = 0;
        for (int i = 0; i < repetitions; i++) {
            if (books.contains(probe)) {
                found++;
            }
        }
        return found;
    }
}
```

#### 5.5. `Main.java`

Последовательно демонстрирует обязательные операции. Результат `Set.add()` показывает отклонение дубликата; размер множества остаётся равен десяти.

```java
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** Демонстрация обязательных заданий лабораторной №4. */
public final class Main {
    private static final int YEAR_BOUNDARY = 2000;

    private Main() { }

    /**
     * Запускает демонстрацию коллекций и замер поиска.
     * @param args аргументы командной строки не используются
     */
    public static void main(String[] args) {
        List<Book> books = SampleBooks.create();
        printBooks("Исходный список (10 книг)", books);
        printBooks("Сортировка по году", BookCollections.sortByYear(books));
        printBooks("Сортировка по автору", BookCollections.sortByAuthor(books));
        List<Book> recent = BookCollections.publishedAfter(books, YEAR_BOUNDARY);
        printBooks("Книги после " + YEAR_BOUNDARY + " года (по названию)", recent);
        System.out.println("Названия через map(): " + BookCollections.titles(recent));
        demonstrateSet(books);
        printGroups(BookCollections.groupByAuthor(books));
        System.out.println("Количество книг по авторам: " + BookCollections.countByAuthor(books));
        SearchBenchmark.run();
    }

    private static void printBooks(String heading, List<Book> books) {
        System.out.println("\n" + heading + ":");
        books.forEach(System.out::println);
    }

    private static void demonstrateSet(List<Book> books) {
        Set<Book> uniqueBooks = new HashSet<>(books);
        Book first = books.get(0);
        Book duplicate = new Book(first.getId(), first.getTitle(), first.getAuthor(), first.getYear());
        System.out.println("\nHashSet: " + uniqueBooks.size() + " книг до добавления копии.");
        System.out.println("Копия равна оригиналу: " + first.equals(duplicate));
        System.out.println("Дубликат добавлен: " + uniqueBooks.add(duplicate));
        System.out.println("После добавления: " + uniqueBooks.size() + " книг.");
    }

    private static void printGroups(Map<String, List<Book>> groups) {
        System.out.println("\nГруппировка по автору:");
        groups.forEach((author, books) ->
                System.out.println(author + " -> " + BookCollections.titles(books)));
    }
}
```

#### 5.6. Сборка и тесты

Конфигурация [pom.xml](pom.xml) задаёт `release=17`, UTF-8, каталоги `src` и `test`, JUnit Jupiter 5.11.4 и `Main-Class: Main` в JAR. Код не зависит от библиотек времени выполнения.

[BookTest.java](test/BookTest.java) содержит восемь тестов: поля конструктора; рефлексивность, симметричность и транзитивность равенства; равенство хешей; сравнение с `null` и другим типом; участие каждого поля; отклонение копии, поиск и удаление равной книги; недопустимые данные и допустимые границы.

[BookCollectionsTest.java](test/BookCollectionsTest.java) содержит восемь тестов: десять уникальных книг и независимость списков; обе сортировки с дополнительным полем и сохранением исходника; строгая граница 2000; порядок названий; состав групп; агрегированные количества; пустой ввод.

Время поиска не проверяется через утверждение «HashSet обязан быть быстрее»: такое условие зависело бы от оборудования, JVM и нагрузки. Проверка найденных объектов встроена в запуск замера.

---

### 6. Протокол работы программы

Ниже — фактический текстовый вывод проверочного запуска агентом 09.10.2026. Полная копия: [docs/verification-output.txt](docs/verification-output.txt). Порядок и результаты обработки книг воспроизводимы; измеренные времена при следующем запуске будут другими.

```text
Исходный список (10 книг):
#1: "Чистый код" — Роберт Мартин (2008)
#2: "Эффективная Java" — Джошуа Блох (2018)
#3: "Философия Java" — Брюс Эккель (2006)
#4: "Чистая архитектура" — Роберт Мартин (2017)
#5: "Java: полное руководство" — Герберт Шилдт (2021)
#6: "Алгоритмы" — Роберт Седжвик (2011)
#7: "Совершенный код" — Стив Макконнелл (2004)
#8: "Программист-прагматик" — Эндрю Хант (1999)
#9: "Объектно-ориентированное проектирование" — Гради Буч (2000)
#10: "Чистый Agile" — Роберт Мартин (2019)

Сортировка по году:
#8: "Программист-прагматик" — Эндрю Хант (1999)
#9: "Объектно-ориентированное проектирование" — Гради Буч (2000)
#7: "Совершенный код" — Стив Макконнелл (2004)
#3: "Философия Java" — Брюс Эккель (2006)
#1: "Чистый код" — Роберт Мартин (2008)
#6: "Алгоритмы" — Роберт Седжвик (2011)
#4: "Чистая архитектура" — Роберт Мартин (2017)
#2: "Эффективная Java" — Джошуа Блох (2018)
#10: "Чистый Agile" — Роберт Мартин (2019)
#5: "Java: полное руководство" — Герберт Шилдт (2021)

Сортировка по автору:
#3: "Философия Java" — Брюс Эккель (2006)
#5: "Java: полное руководство" — Герберт Шилдт (2021)
#9: "Объектно-ориентированное проектирование" — Гради Буч (2000)
#2: "Эффективная Java" — Джошуа Блох (2018)
#4: "Чистая архитектура" — Роберт Мартин (2017)
#10: "Чистый Agile" — Роберт Мартин (2019)
#1: "Чистый код" — Роберт Мартин (2008)
#6: "Алгоритмы" — Роберт Седжвик (2011)
#7: "Совершенный код" — Стив Макконнелл (2004)
#8: "Программист-прагматик" — Эндрю Хант (1999)

Книги после 2000 года (по названию):
#5: "Java: полное руководство" — Герберт Шилдт (2021)
#6: "Алгоритмы" — Роберт Седжвик (2011)
#7: "Совершенный код" — Стив Макконнелл (2004)
#3: "Философия Java" — Брюс Эккель (2006)
#4: "Чистая архитектура" — Роберт Мартин (2017)
#10: "Чистый Agile" — Роберт Мартин (2019)
#1: "Чистый код" — Роберт Мартин (2008)
#2: "Эффективная Java" — Джошуа Блох (2018)
Названия через map(): [Java: полное руководство, Алгоритмы, Совершенный код, Философия Java, Чистая архитектура, Чистый Agile, Чистый код, Эффективная Java]

HashSet: 10 книг до добавления копии.
Копия равна оригиналу: true
Дубликат добавлен: false
После добавления: 10 книг.

Группировка по автору:
Брюс Эккель -> [Философия Java]
Герберт Шилдт -> [Java: полное руководство]
Гради Буч -> [Объектно-ориентированное проектирование]
Джошуа Блох -> [Эффективная Java]
Роберт Мартин -> [Чистый код, Чистая архитектура, Чистый Agile]
Роберт Седжвик -> [Алгоритмы]
Стив Макконнелл -> [Совершенный код]
Эндрю Хант -> [Программист-прагматик]
Количество книг по авторам: {Брюс Эккель=1, Герберт Шилдт=1, Гради Буч=1, Джошуа Блох=1, Роберт Мартин=3, Роберт Седжвик=1, Стив Макконнелл=1, Эндрю Хант=1}

Поиск: 100,000 элементов; 100 повторений на случай; 3 раунда.
Раунд 1:
  ArrayList / есть (конец списка): 417.931 мкс/поиск; найдено 100/100
  ArrayList / нет: 423.489 мкс/поиск; найдено 0/100
  HashSet / есть (конец списка): 0.373 мкс/поиск; найдено 100/100
  HashSet / нет: 0.135 мкс/поиск; найдено 0/100
Раунд 2:
  HashSet / есть (конец списка): 0.429 мкс/поиск; найдено 100/100
  HashSet / нет: 0.139 мкс/поиск; найдено 0/100
  ArrayList / есть (конец списка): 391.858 мкс/поиск; найдено 100/100
  ArrayList / нет: 404.781 мкс/поиск; найдено 0/100
Раунд 3:
  ArrayList / есть (конец списка): 408.221 мкс/поиск; найдено 100/100
  ArrayList / нет: 410.182 мкс/поиск; найдено 0/100
  HashSet / есть (конец списка): 0.409 мкс/поиск; найдено 100/100
  HashSet / нет: 0.085 мкс/поиск; найдено 0/100
Времена зависят от JVM и нагрузки; это учебный замер, не JMH.
```

**Место для пользовательского скриншота:** после собственного запуска добавить `docs/images/program-output.png` и раскомментировать строку ниже. Пользовательский снимок пока не предоставлен.

<!-- ![Протокол работы программы](docs/images/program-output.png) -->

В отфильтрованном списке восемь книг. Дубликат отклонён, размер `HashSet` остался равен десяти. Восемь авторов образовали восемь групп, у Роберта Мартина три книги. Во всех раундах существующая книга найдена 100 раз из 100, отсутствующая — 0 раз.

Измеряется только пакет вызовов `contains()` и подсчёт найденных объектов; создание коллекций и печать не входят в длительность. Случаи «конец списка» и «не найдено» требуют полного прохода `ArrayList`. Полученные значения согласуются с линейным поиском в списке и ожидаемым O(1) поиском в хеш-таблице при хорошем распределении хешей. Нельзя обобщать эти числа на любой поиск, все реализации JVM или любые данные. Короткий прогрев, JIT (компиляция во время выполнения), сборка мусора и нагрузка влияют на результат; для строгого сравнения следует использовать JMH с отдельными запусками JVM и более длительными измерениями.

---

### 7. Выписка из журнала применения ИИ

Использован **OpenAI Codex** для анализа задания, подготовки реализации, тестов и отчёта. Упомянутые в методичке YandexGPT и GigaChat фактически не использовались.

Полный [журнал](docs/prompts/README.md) содержит десять этапов и оформлен как **реконструированный сценарий работы**, а не дословная переписка.

| Этап | Результат | Проверка и внесённые изменения |
|---|---|---|
| Анализ задания | Выделена обязательная часть и восемь вопросов | Не добавлены индивидуальные варианты и зависимости на №3 |
| Генерация `equals`/`hashCode` | Все четыре поля участвуют в обоих методах | Равные отдельные объекты проверены в `HashSet`; поля неизменяемы |
| Коллекции и Stream API | Сортировка, фильтрация, группировка, подсчёт | Проверены граница 2000, порядок и сохранение исходного списка |
| Замер поиска | 100 000 книг, два случая, три раунда | Данные и печать исключены из длительности; результат поиска проверяется |
| Финальная проверка | 16 успешных тестов, JAR запущен | Javadoc исправлен после предупреждений; листинги сверены с исходниками |

---

### 8. Ответы на контрольные вопросы

**1. Чем `ArrayList` отличается от `LinkedList`?**

`ArrayList` основан на динамическом массиве: `get(index)` занимает O(1), вставка или удаление в середине — O(n) из-за сдвига. Добавление в конец — амортизированное O(1), отдельное расширение массива — O(n). `LinkedList` — двусвязный список: доступ по индексу O(n), операции на концах O(1). Вставка/удаление через итератор у уже найденного узла выполняется за O(1), но поиск узла по индексу требует прохода. Поэтому нельзя считать любую вставку в `LinkedList` быстрее. Узлы требуют дополнительной памяти; `ArrayList` обычно лучше использует кеш процессора. `contains` в обеих структурах — линейный поиск.

**2. Чем `HashSet` отличается от `TreeSet`?**

`HashSet` не гарантирует порядок обхода, определяет равенство через `hashCode` и `equals`; основные операции ожидаемо O(1) при хорошем распределении хешей. `TreeSet` хранит элементы по natural ordering (естественный порядок через `Comparable`) либо `Comparator`, выполняет поиск, добавление и удаление за O(log n) и поддерживает диапазоны. Для него элемент является дубликатом, если сравнение возвращает ноль. Например, сравнение только по году ошибочно объединит разные книги одного года. `HashSet` допускает `null`; `TreeSet` с естественным порядком его не допускает, с подходящим компаратором может допускать.

**3. Почему для `Set` нужно переопределять `equals()` и `hashCode()`?**

Для `HashSet` методы нужны, если уникальность должна определяться значениями полей: стандартный `Object.equals` сравнивает ссылки. Хеш выбирает область поиска, `equals` проверяет равенство кандидатов. Равные объекты должны иметь одинаковый хеш; обратное неверно. После добавления нельзя менять поля, влияющие на равенство и хеш. Уточнение: это требование относится к хеш-множествам; `TreeSet` использует `Comparable` или `Comparator` и не требует хеширования для поиска.

**4. Что такое Stream API? Какие операции являются промежуточными, какие — терминальными?**

Это API обработки элементов из коллекции или другого источника в виде цепочки операций. Промежуточные `filter`, `map`, `sorted`, `distinct`, `limit` возвращают поток и выполняются лениво. Терминальные `collect`, `toList`, `count`, `reduce`, `forEach`, `anyMatch` запускают обработку и возвращают результат либо побочный эффект. Один поток нельзя повторно использовать после терминальной операции. `map` преобразует элемент, `filter` отбирает элементы; поток сам не является хранилищем данных.

**5. Что делает `Collectors.groupingBy()`?**

Создаёт collector (объект, задающий способ сбора результата), распределяющий элементы по ключу классификатора. `groupingBy(Book::getAuthor)` создаёт `Map<String, List<Book>>`. Дополнительный downstream collector (сборщик внутри каждой группы) меняет результат: `Collectors.counting()` даёт `Map<String, Long>`. Можно задать фабрику карты, например `TreeMap::new`, чтобы ключи имели порядок. Без явной фабрики порядок ключей не гарантирован.

**6. Как измерить производительность различных реализаций коллекций?**

Подготовить одинаковые данные и поисковые запросы, создать коллекции до измерения, выполнить прогрев, затем измерить разность `System.nanoTime()` до и после многократной операции. Результат операции нужно использовать и проверить; печать должна быть за пределами замера. Следует сравнивать одинаковые сценарии: наличие в начале/середине/конце списка и отсутствие. Повторить измерения, указать размер данных, число повторений, JVM и ограничения. В этой работе измерены конец списка и отсутствие на 100 000 книгах; время создания коллекций и расход памяти не сравниваются. Для точных JVM-бенчмарков используют JMH, который помогает учитывать JIT, устранение неиспользуемых вычислений и другие искажения.

**7. Что такое `Comparator`? Чем отличается от `Comparable`?**

`Comparator<T>` — отдельная стратегия сравнения с методом `compare(a, b)`. Позволяет задавать много порядков без изменения класса. `Comparable<T>` реализуется самим классом и задаёт естественный порядок через `compareTo(other)`. Отрицательный результат означает «меньше», ноль — равенство по порядку, положительный — «больше». В работе книга не имеет единственного естественного порядка, поэтому выбраны внешние компараторы. Для чисел лучше `comparingInt`/`Integer.compare`, а не вычитание, которое может переполниться.

**8. Как отсортировать коллекцию по нескольким полям?**

Составить цепочку `Comparator.comparing...().thenComparing...()`. Следующий компаратор используется, когда предыдущий вернул ноль. Пример из проекта:

```java
Collections.sort(sorted, Comparator.comparingInt(Book::getYear)
        .thenComparing(Book::getTitle));
```

При необходимости добавляют автора и ID. `reversed()` обращает тот компаратор, к которому применён: вызов в конце всей цепочки обратит порядок по всем полям, а обращение вложенного компаратора — только соответствующее поле. `List` можно сортировать непосредственно; `HashSet` для отсортированного вывода преобразуют в список либо используют `stream().sorted(...)`.

---

### 9. Выводы по работе

Реализованы обязательные операции с `List`, `Set` и `Map` на единой предметной области книг. Проверены сортировка по нескольким полям, строгая фильтрация, преобразование названий и группировка с подсчётом. Неизменяемый `Book` и согласованные `equals`/`hashCode` обеспечивают отклонение дубликатов и корректный поиск отдельного равного объекта в `HashSet`.

На 100 000 элементах выполнен учебный замер поиска. В проверочном запуске `HashSet` оказался быстрее полного прохода `ArrayList`; вывод относится к выбранным сценариям и условиям. Сборка и 16 модульных тестов прошли, JAR запущен. Индивидуальный вариант не выполнялся.

---

### 10. Команды сборки, тестирования и запуска

Требуются JDK 17+ и Maven. Из корня репозитория:

```bash
cd lab04-collections
mvn clean verify
java -jar target/lab04-collections-1.0.0.jar
```

Отдельно тесты: `mvn test`. В IntelliJ IDEA открыть `pom.xml` как Maven-проект и запустить `Main.main()`.

**Фактическая проверка:** Maven 3.9.16 из IntelliJ IDEA, OpenJDK 25.0.4.1, компиляция с `release=17`. `clean verify` завершился `BUILD SUCCESS`: 16 тестов, 0 ошибок, 0 отказов, 0 пропусков. JAR успешно запущен. `javap -verbose` показал `major version: 61`, то есть формат классов Java 17. Непосредственно на JDK 17 запуск отдельно не проводился. `javadoc -Xdoclint:all` после исправления комментариев завершился без предупреждений.

В этой среде Maven не находится в `PATH`, поэтому использована команда из корня репозитория с уже заполненным временным кешем:

```bash
"/home/ilyshka/Загрузки/idea-2026.2.2/idea-IU-262.10315.125/plugins/maven-plugin/lib/maven3/bin/mvn" -o -Dmaven.repo.local=/tmp/lab02-maven-repository -B -f lab04-collections/pom.xml clean verify
java -jar lab04-collections/target/lab04-collections-1.0.0.jar
```

Для обычной сборки на другом компьютере использовать первые команды с `mvn`: временный кеш и путь к IntelliJ не являются зависимостями проекта. При первой сборке Maven потребуется доступ к репозиторию зависимостей.
