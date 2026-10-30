import exceptions.InvalidBookDataException;
import java.time.Year;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class BookTest {
    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {" ", "\t"})
    void rejectsInvalidTitleAndAuthor(String value) {
        assertThrows(InvalidBookDataException.class, () -> new Book(value, "Автор", 2000));
        assertThrows(InvalidBookDataException.class, () -> new Book("Книга", value, 2000));
    }

    @Test
    void acceptsYearBoundaries() {
        Book book = new Book("Книга", "Автор", 1);
        assertEquals(1, book.getYear());
        book.setYear(Year.now().getValue());
        assertEquals(Year.now().getValue(), book.getYear());
    }

    @Test
    void rejectsInvalidYearsWithoutChangingState() {
        Book book = new Book("Книга", "Автор", 2000);
        for (int year : new int[]{-1, 0, Year.now().getValue() + 1}) {
            assertThrows(InvalidBookDataException.class, () -> book.setYear(year));
            assertEquals(2000, book.getYear());
        }
    }

    @Test
    void rejectedTextUpdatePreservesPreviousValue() {
        Book book = new Book("Книга", "Автор", 2000);
        assertThrows(InvalidBookDataException.class, () -> book.setTitle(" "));
        assertThrows(InvalidBookDataException.class, () -> book.setAuthor(null));
        assertEquals("Книга", book.getTitle());
        assertEquals("Автор", book.getAuthor());
    }
}
