import exceptions.BookNotFoundException;
import exceptions.InvalidBookDataException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

class FileLoadingTest {
    @TempDir
    Path directory;

    @Test
    void preservesCauseOfInvalidYear() {
        InvalidBookDataException exception = assertThrows(InvalidBookDataException.class,
                () -> Main.parseBook("Книга;Автор;текст"));
        assertInstanceOf(NumberFormatException.class, exception.getCause());
    }

    @Test
    void checksFieldCountIncludingTrailingEmptyYear() {
        assertThrows(InvalidBookDataException.class, () -> Main.parseBook("Книга;Автор"));
        assertThrows(InvalidBookDataException.class, () -> Main.parseBook("Книга;Автор;2000;лишнее"));
        assertThrows(InvalidBookDataException.class, () -> Main.parseBook("Книга;Автор;"));
    }

    @Test
    void loadsValidLinesAfterInvalidOnesAndClosesReader() throws IOException {
        Path file = directory.resolve("books.txt");
        Files.writeString(file, "Первая;Автор;2000\n;Автор;2000\nПлохая;Автор;текст\nВторая;Автор;2001\n");
        Repository<Book> repository = new Repository<>();
        Main.loadBooks(file, repository);
        assertEquals(2, repository.findAll().size());
        assertEquals("Вторая", repository.findAll().get(1).getTitle());
        Files.delete(file);
        assertFalse(Files.exists(file));
    }

    @Test
    void missingFileDoesNotDiscardExistingBooks() {
        Repository<Book> repository = new Repository<>();
        repository.save(new Book("Книга", "Автор", 2000));
        assertDoesNotThrow(() -> Main.loadBooks(directory.resolve("absent.txt"), repository));
        assertEquals(1, repository.findAll().size());
    }

    @Test
    void customExceptionsKeepMessageAndCause() {
        IOException cause = new IOException("Причина");
        BookNotFoundException checked = new BookNotFoundException("Поиск", cause);
        InvalidBookDataException unchecked = new InvalidBookDataException("Данные", cause);
        assertEquals("Поиск", checked.getMessage());
        assertSame(cause, checked.getCause());
        assertEquals("Данные", unchecked.getMessage());
        assertSame(cause, unchecked.getCause());
    }
}
