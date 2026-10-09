import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookCollectionsTest {
    @Test
    void sampleContainsTenDistinctBooksAndReturnsFreshList() {
        List<Book> books = SampleBooks.create();
        assertEquals(10, books.size());
        assertEquals(10, new HashSet<>(books).size());
        books.clear();
        assertEquals(10, SampleBooks.create().size());
    }

    @Test
    void sortsYearsAndUsesTitleForTiesWithoutChangingSource() {
        Book a = new Book(1, "А", "Автор", 2001);
        Book b = new Book(2, "Б", "Автор", 2001);
        Book old = new Book(3, "Я", "Автор", 1999);
        List<Book> source = new ArrayList<>(List.of(b, a, old));
        assertEquals(List.of(old, a, b), BookCollections.sortByYear(source));
        assertEquals(List.of(b, a, old), source);
    }

    @Test
    void sortsAuthorsAndUsesTitleForTiesWithoutChangingSource() {
        Book a = new Book(1, "А", "Б", 2001);
        Book b = new Book(2, "Б", "Б", 1999);
        Book firstAuthor = new Book(3, "Я", "А", 2020);
        List<Book> source = List.of(b, a, firstAuthor);
        assertEquals(List.of(firstAuthor, a, b), BookCollections.sortByAuthor(source));
        assertEquals(List.of(b, a, firstAuthor), source);
    }

    @Test
    void filtersStrictlyAfterBoundaryAndSortsTitles() {
        Book boundary = new Book(1, "Граница", "Автор", 2000);
        Book old = new Book(2, "Старая", "Автор", 1999);
        Book a = new Book(3, "А", "Автор", 2001);
        Book b = new Book(4, "Б", "Автор", 2020);
        assertEquals(List.of(a, b), BookCollections.publishedAfter(List.of(b, old, a, boundary), 2000));
        assertEquals(8, BookCollections.publishedAfter(SampleBooks.create(), 2000).size());
    }

    @Test
    void mapsTitlesInInputOrder() {
        List<Book> books = List.of(new Book(1, "Б", "Автор", 2020),
                new Book(2, "А", "Автор", 2020));
        assertEquals(List.of("Б", "А"), BookCollections.titles(books));
    }

    @Test
    void groupsAllBooksAndPreservesOrderWithinGroup() {
        List<Book> books = SampleBooks.create();
        Map<String, List<Book>> groups = BookCollections.groupByAuthor(books);
        assertEquals(8, groups.size());
        assertEquals(List.of(books.get(0), books.get(3), books.get(9)), groups.get("Роберт Мартин"));
        assertEquals(10, groups.values().stream().mapToInt(List::size).sum());
    }

    @Test
    void aggregatesCountsByAuthor() {
        Map<String, Long> counts = BookCollections.countByAuthor(SampleBooks.create());
        assertEquals(3L, counts.get("Роберт Мартин"));
        assertEquals(1L, counts.get("Брюс Эккель"));
        assertEquals(10L, counts.values().stream().mapToLong(Long::longValue).sum());
    }

    @Test
    void handlesEmptyInput() {
        assertAll(() -> assertTrue(BookCollections.sortByYear(List.of()).isEmpty()),
                () -> assertTrue(BookCollections.sortByAuthor(List.of()).isEmpty()),
                () -> assertTrue(BookCollections.publishedAfter(List.of(), 2000).isEmpty()),
                () -> assertTrue(BookCollections.titles(List.of()).isEmpty()),
                () -> assertTrue(BookCollections.groupByAuthor(List.of()).isEmpty()),
                () -> assertTrue(BookCollections.countByAuthor(List.of()).isEmpty()));
    }
}
