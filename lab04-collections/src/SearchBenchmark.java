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
