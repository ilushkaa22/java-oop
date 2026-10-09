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
