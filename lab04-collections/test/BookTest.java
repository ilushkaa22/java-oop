import java.time.Year;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    private Book book() { return new Book(1, "Java", "Автор", 2020); }

    @Test
    void exposesConstructorValues() {
        Book book = book();
        assertAll(() -> assertEquals(1, book.getId()),
                () -> assertEquals("Java", book.getTitle()),
                () -> assertEquals("Автор", book.getAuthor()),
                () -> assertEquals(2020, book.getYear()));
    }

    @Test
    void equalityIsReflexiveSymmetricAndTransitive() {
        Book a = book();
        Book b = book();
        Book c = book();
        assertAll(() -> assertEquals(a, a), () -> assertEquals(a, b),
                () -> assertEquals(b, a), () -> assertEquals(b, c),
                () -> assertEquals(a, c), () -> assertEquals(a.hashCode(), b.hashCode()),
                () -> assertEquals(a.hashCode(), a.hashCode()));
    }

    @Test
    void rejectsNullAndOtherTypeInEquality() {
        assertNotEquals(book(), null);
        assertNotEquals(book(), "Java");
    }

    @Test
    void eachFieldParticipatesInEquality() {
        Book original = book();
        assertAll(() -> assertNotEquals(original, new Book(2, "Java", "Автор", 2020)),
                () -> assertNotEquals(original, new Book(1, "Другое", "Автор", 2020)),
                () -> assertNotEquals(original, new Book(1, "Java", "Другой", 2020)),
                () -> assertNotEquals(original, new Book(1, "Java", "Автор", 2019)));
    }

    @Test
    void hashSetRejectsCopyAndFindsAndRemovesEqualObject() {
        Set<Book> books = new HashSet<>();
        assertTrue(books.add(book()));
        assertFalse(books.add(book()));
        assertEquals(1, books.size());
        assertTrue(books.contains(book()));
        assertTrue(books.remove(book()));
        assertTrue(books.isEmpty());
    }

    @Test
    void rejectsInvalidIdAndYear() {
        assertAll(() -> assertThrows(IllegalArgumentException.class,
                        () -> new Book(-1, "Java", "Автор", 2020)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Book(1, "Java", "Автор", -1)),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> new Book(1, "Java", "Автор", Year.now().getValue() + 1)));
    }

    @Test
    void rejectsEmptyAndNullText() {
        for (String invalid : new String[] {null, "", " \t\n"}) {
            assertThrows(IllegalArgumentException.class, () -> new Book(1, invalid, "Автор", 2020));
            assertThrows(IllegalArgumentException.class, () -> new Book(1, "Java", invalid, 2020));
        }
    }

    @Test
    void acceptsBoundaryValues() {
        assertEquals(0, new Book(0, "Java", "Автор", 0).getYear());
        int current = Year.now().getValue();
        assertEquals(current, new Book(1, "Java", "Автор", current).getYear());
    }
}
