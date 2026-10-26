# Отчёт по лабораторной работе №5

**Текущее состояние: учебная заготовка для самостоятельной реализации.** `Main` и комментарии сохранены; методы `Pair` и `Repository` содержат TODO. `swap` объявлен в `Pair`. Пока TODO не заполнены, запуск завершится `UnsupportedOperationException`, а функциональные тесты не пройдут. Успешные проверки предыдущей версии не относятся к этой заготовке.

### 1. Титульный лист

**Лабораторная работа №5**  
по дисциплине: «Объектно-ориентированное программирование»  
на тему: «Generics: универсальные структуры данных»

**Выполнил:**  
Студент группы ИИ21/1  
Саврико Илья

---

### 2. Цель и задачи работы

**Цель:** освоить обобщённые классы и методы, ограничения типов и стирание типов.

**Задачи:**

1. Реализовать `Pair<A, B>` с полями `first`, `second`, конструктором и getter-методами.
2. Реализовать обобщённый `swap` для обмена элементов массива.
3. Переписать `Pair`, ограничив типы через `Comparable`, добавить `min()` и `max()`.
4. Создать `Repository<T>` с методами `save(T)`, `findById(long)`, `delete(long)`, `findAll()`.

Источник: [Lab5.md](https://github.com/Natali2531/Study/blob/main/Лабораторные%20работы%20по%20ООП/Lab5.md), просмотрен 26.10.2026. Выполнена обязательная часть. Индивидуальный вариант не запрошен.

---

### 3. Краткое теоретическое введение

Generics — обобщения, позволяющие задавать типы при использовании класса или метода. Параметры `A`, `B`, `T` обозначают типы, а `Integer` и `String` — конкретные аргументы типов. Проверка совместимости выполняется при компиляции. Используются ссылочные типы: вместо примитивного `int` нужен `Integer`.

Ограничение `A extends Comparable<A>` разрешает сравнение через `compareTo()`. В итоговом `Pair<A, B>` ограничение `B extends A` означает, что второе значение тоже реализует `Comparable<A>` и совместимо с первым. Произвольные независимые сравнимые типы нельзя сравнить друг с другом: `Integer` и `String` имеют собственные порядки. `min()` и `max()` возвращают общий тип `A`. См. [Oracle: ограничения типов](https://docs.oracle.com/javase/tutorial/java/generics/bounded.html).

Type erasure — стирание типов: параметры заменяются верхними границами либо `Object`, компилятор добавляет необходимые приведения. Разные аргументы типа не создают отдельные runtime-классы. Поэтому `new T()` и `new T[n]` недопустимы; для создания объекта можно передать фабрику. См. [Oracle: стирание типов](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html).

---

### 4. Листинги программного кода с пояснениями

#### 4.1. Класс Pair

```java
import java.util.Objects;

/**
 * Пара сравнимых значений с конструктором и getter-методами.
 * @param <A> тип первого значения, поддерживающий сравнение
 * @param <B> тип второго значения: A или его подкласс
 */
public final class Pair<A extends Comparable<A>, B extends A> {
    private final A first;
    private final B second;

    /**
     * Создаёт пару.
     * @param first первое значение
     * @param second второе значение
     * @throws NullPointerException если значение равно null
     */
    public Pair(A first, B second) {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }

    /**
     * Возвращает первое значение.
     * @return первое значение
     */
    public A getFirst() {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }

    /**
     * Возвращает второе значение.
     * @return второе значение
     */
    public B getSecond() {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }

    /**
     * Возвращает меньшее значение.
     * @return минимум; при равенстве первое значение
     */
    public A min() {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }

    /**
     * Возвращает большее значение.
     * @return максимум; при равенстве первое значение
     */
    public A max() {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }

    /**
     * Меняет местами два элемента исходного массива.
     * @param <T> тип элементов
     * @param array исходный массив
     * @param i первый индекс
     * @param j второй индекс
     * @throws NullPointerException если массив равен null
     * @throws IndexOutOfBoundsException если хотя бы один индекс некорректен
     */
    public static <T> void swap(T[] array, int i, int j) {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }
}
```

#### 4.2. Класс Repository

```java
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Хранилище в памяти с собственными последовательными ключами, без потокобезопасности.
 * @param <T> тип сохраняемых объектов
 */
public final class Repository<T> {
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
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }

    /**
     * Находит объект по ключу хранилища.
     * @param id ключ
     * @return объект или null, если ключ отсутствует
     */
    public T findById(long id) {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }

    /**
     * Удаляет объект по ключу.
     * @param id ключ
     * @return true, если объект существовал и был удалён
     */
    public boolean delete(long id) {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }

    /**
     * Возвращает отдельный изменяемый список в порядке сохранения.
     * @return поверхностная копия значений; сами объекты не копируются
     */
    public List<T> findAll() {
        // TODO: реализовать самостоятельно.
        throw new UnsupportedOperationException("Метод ещё не реализован");
    }
}
```

#### 4.3. Класс Main

```java
import java.util.Arrays;

/** Демонстрация пары, обмена элементов и обобщённого хранилища. */
public final class Main {
    private Main() { }

    /**
     * Запускает демонстрацию.
     * @param args аргументы не используются
     */
    public static void main(String[] args) {
        demonstratePair();
        demonstrateSwap();
        demonstrateRepository();
    }

    private static void demonstratePair() {
        System.out.println("=== Pair ===");
        Pair<Integer, Integer> numbers = new Pair<>(20, 10);
        System.out.println("first = " + numbers.getFirst());
        System.out.println("second = " + numbers.getSecond());
        System.out.println("min = " + numbers.min());
        System.out.println("max = " + numbers.max());
        Pair<String, String> words = new Pair<>("Java", "Algorithms");
        System.out.println("Строки: min = " + words.min() + ", max = " + words.max());
    }

    private static void demonstrateSwap() {
        System.out.println("\n=== swap ===");
        Integer[] numbers = {10, 20, 30};
        System.out.println("До: " + Arrays.toString(numbers));
        Pair.swap(numbers, 0, 2);
        System.out.println("После: " + Arrays.toString(numbers));
        String[] words = {"Java", "SQL"};
        Pair.swap(words, 0, 1);
        System.out.println("Строки после обмена: " + Arrays.toString(words));
    }

    private static void demonstrateRepository() {
        System.out.println("\n=== Repository<String> ===");
        Repository<String> repository = new Repository<>();
        long firstId = repository.save("Java");
        long secondId = repository.save("SQL");
        System.out.println("ID: " + firstId + ", " + secondId);
        System.out.println("Все элементы: " + repository.findAll());
        System.out.println("Поиск ID " + firstId + ": " + repository.findById(firstId));
        System.out.println("Удаление ID " + secondId + ": " + repository.delete(secondId));
        System.out.println("Повторное удаление: " + repository.delete(secondId));
        System.out.println("Поиск удалённого: " + repository.findById(secondId));
        System.out.println("Осталось: " + repository.findAll());
    }
}
```

#### 4.4. Тесты

В каталоге [test](test) находятся `PairTest` (5 тестов), `PairSwapTest` (4 теста) и `RepositoryTest` (7 тестов). Проверяются getter-методы, минимумы и максимумы, равные значения, обмен элементов, ошибочные индексы, null, сохранение и удаление, порядок, независимость списков и счётчиков.

---

### 5. Протокол работы программы

Вывод предыдущей завершённой версии, сохранённый как ориентир ожидаемого результата после реализации TODO; копия: [docs/verification-output.txt](docs/verification-output.txt).

```text
=== Pair ===
first = 20
second = 10
min = 10
max = 20
Строки: min = Algorithms, max = Java

=== swap ===
До: [10, 20, 30]
После: [30, 20, 10]
Строки после обмена: [SQL, Java]

=== Repository<String> ===
ID: 1, 2
Все элементы: [Java, SQL]
Поиск ID 1: Java
Удаление ID 2: true
Повторное удаление: false
Поиск удалённого: null
Осталось: [Java]
```

**Место для пользовательского скриншота:** сохранить снимок консоли в `docs/images/program-output.png` и добавить строку `![Протокол запуска](docs/images/program-output.png)`. Текст выше не является пользовательским скриншотом.

---

### 6. Выписка из журнала применения ИИ

Использован **OpenAI Codex**. YandexGPT и GigaChat не использовались. Полный [журнал](docs/prompts/README.md) — реконструированный сценарий, а не дословная переписка.

| Этап | Результат | Критическая проверка |
| --- | --- | --- |
| Pair | Поля, конструктор, getter-методы, min и max | Типы взаимно совместимы для сравнения |
| swap | Обмен элементов T[] | Проверены оба индекса до изменения |
| Repository | Четыре требуемых метода | Отдельный список, отсутствующий ID, повторное удаление |
| Текущее состояние | Три класса, TODO и 16 тестов для самопроверки | Полную проверку выполнить после заполнения TODO |

---

### 7. Ответы на контрольные вопросы

**1. Что такое generics? Какую проблему они решают?**

Generics — механизм параметризации классов, интерфейсов и методов типами. Он позволяет повторно использовать алгоритмы для разных типов, сохраняя проверку совместимости при компиляции. Например, `Repository<String>` не позволит сохранить число, а результат `findById()` имеет тип `String`. Без generics хранилище на `Object` потребовало бы ручных приведений и могло бы завершиться `ClassCastException` при ошибке типа.

**2. Что такое параметр типа? Какие соглашения об именовании существуют?**

Параметр типа — переменная типа в объявлении, например `T` в `Repository<T>`. Конкретный `String` в `Repository<String>` — аргумент типа. Обычно применяют `T` (type), `E` (element), `K` (key), `V` (value), `N` (number); для нескольких типов — `A`, `B` либо `T`, `U`. Это соглашения, а не специальные ключевые слова. Параметр обобщённого метода объявляется перед возвращаемым типом: `public static <T> void swap(...)`.

**3. Что такое ограничители типов (`extends`)?**

Верхняя граница ограничивает множество допустимых типов и даёт доступ к методам границы. `A extends Comparable<A>` означает, что A поддерживает сравнение с A. В этой работе `B extends A` делает второе значение совместимым с первым, поэтому `first.compareTo(second)` корректен. В объявлении параметра типа `extends` используется и для классов, и для интерфейсов. Несколько границ записываются через `&`; класс, если он есть, должен стоять первым. Само наличие `Comparable` у двух независимых типов не делает их взаимно сравнимыми. См. [Oracle](https://docs.oracle.com/javase/tutorial/java/generics/bounded.html).

**4. Что такое стирание типов (type erasure)?**

При компиляции параметры типов заменяются верхней границей либо `Object`. Компилятор вставляет необходимые приведения и вспомогательные bridge methods. Поэтому создание `Repository<String>` не порождает отдельный runtime-класс: обе специализации `Repository` используют один класс. Однако стирание не означает полного отсутствия generic-метаданных: сигнатуры объявлений доступны через reflection. Проверять `instanceof Repository<String>` нельзя; проверка `instanceof Repository<?>` допустима. См. [Oracle](https://docs.oracle.com/javase/tutorial/java/generics/erasure.html).

**5. Можно ли создать экземпляр параметра типа (`new T()`)?**

Нет. В общем случае конкретный класс `T` и наличие подходящего конструктора неизвестны. Для создания передают фабрику, например `Supplier<T>` и вызывают `get()`. Можно также передать `Class<T>` и использовать `getDeclaredConstructor().newInstance()`, обрабатывая исключения reflection. В данной работе новые объекты создаёт вызывающий код и передаёт их в `save(T)`. Аргументами generics могут быть только ссылочные типы, поэтому вместо `int` используют `Integer`. См. [Oracle: ограничения generics](https://docs.oracle.com/javase/tutorial/java/generics/restrictions.html).

---

### 8. Выводы по работе

Реализованы обобщённая пара с ограничениями типов, метод обмена элементов массива и универсальное хранилище. Примеры на числах и строках показывают повторное использование одного кода. Разобраны проверка типов при компиляции, интерфейс `Comparable` и стирание типов.

---

### 9. Сборка, тестирование и запуск

Требуются JDK 17+ и Maven. Из корня репозитория:

```bash
cd lab05-generics
mvn clean verify
java -jar target/lab05-generics-1.0.0.jar
```

Отдельный запуск тестов: `mvn test`. В IntelliJ IDEA открыть `pom.xml` как Maven-проект и запустить `Main.main()`.

**Проверка заготовки:** выполняется компиляция основных и тестовых классов без запуска функциональных тестов. Полную проверку `mvn clean verify` и запуск приложения нужно выполнить после реализации TODO. Прежние 16 успешных тестов относятся к предыдущей завершённой версии.

В этой среде Maven отсутствует в PATH; использована команда из корня репозитория:

```bash
"/home/ilyshka/Загрузки/idea-2026.2.2/idea-IU-262.10315.125/plugins/maven-plugin/lib/maven3/bin/mvn" -o -Dmaven.repo.local=/tmp/lab05-maven-repository -B -f lab05-generics/pom.xml clean verify
```

Временный кеш заполнен ранее; для обычной первой сборки командой `mvn` нужен доступ к репозиторию зависимостей.
