import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LibraryBookTest {
    @Test
    void abstractTypeDispatchesReadingAndDescription() {
        LibraryBook[] books = {
                new PrintedBook(1, "Книга", "Автор", 2000),
                new EBook(2, "Книга", "Автор", 2000)
        };
        assertEquals("Читать бумажные страницы", books[0].read());
        assertEquals("Читать на электронной читалке", books[1].read());
        assertEquals("\"Книга\" — Автор (2000) — печатная книга", books[0].getDescription());
        assertEquals("\"Книга\" — Автор (2000) — электронная книга", books[1].getDescription());
    }

    @Test
    void inheritedToStringAndOverloadUseOverriddenDescription() {
        Book book = new EBook(2, "Книга", "Автор", 2000);
        assertEquals(book.getDescription(), book.toString());
        assertEquals(book.getDescription(), book.getDescription(false));
        assertEquals("Книга (2000)", book.getDescription(true));
    }

    @Test
    void employeeAndBookShareIndependentReportingContract() {
        List<Reportable> reporters = List.of(
                new Manager("Имя", "Отдел", 0, 0),
                new PrintedBook(1, "Книга", "Автор", 2000));
        assertEquals("Отчёт менеджера Имя: подчинённых — 0; список подчинённых: [заглушка]",
                reporters.get(0).generateReport());
        assertEquals("\"Книга\" — Автор (2000) — печатная книга; Читать бумажные страницы",
                reporters.get(1).generateReport());
    }

    @Test
    void subclassesUseSharedCounterAndBaseValidation() {
        int before = Book.getCounter();
        new PrintedBook(1, "Книга", "Автор", 2000);
        new EBook(2, "Книга", "Автор", 2000);
        assertEquals(before + 2, Book.getCounter());
        assertThrows(IllegalArgumentException.class, () -> new PrintedBook(-1, "Книга", "Автор", 2000));
        assertThrows(IllegalArgumentException.class, () -> new EBook(3, " ", "Автор", 2000));
        assertEquals(before + 2, Book.getCounter());
    }
}
