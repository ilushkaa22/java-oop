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
