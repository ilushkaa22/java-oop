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
