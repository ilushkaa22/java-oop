import exceptions.BookNotFoundException;
import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {
    @Test
    void savesFindsAndDeletes() throws BookNotFoundException {
        Repository<Book> repository = new Repository<>();
        Book book = new Book("Книга", "Автор", 2000);
        long id = repository.save(book);
        assertSame(book, repository.findById(id));
        assertTrue(repository.delete(id));
        assertThrows(BookNotFoundException.class, () -> repository.findById(id));
        assertTrue(repository.findAll().isEmpty());
    }

    @Test
    void reportsMissingSearchAndDeletion() {
        Repository<Book> repository = new Repository<>();
        assertThrows(BookNotFoundException.class, () -> repository.findById(999));
        assertThrows(BookNotFoundException.class, () -> repository.delete(999));
    }

    @Test
    void keysAreIndependentAndSnapshotDoesNotChangeStorage() throws BookNotFoundException {
        Repository<Book> repository = new Repository<>();
        Book book = new Book("Книга", "Автор", 2000);
        long first = repository.save(book);
        long second = repository.save(book);
        assertNotEquals(first, second);
        List<Book> snapshot = repository.findAll();
        snapshot.clear();
        repository.delete(first);
        assertSame(book, repository.findById(second));
    }

    @Test
    void rejectsNullWithoutConsumingKey() {
        Repository<Book> repository = new Repository<>();
        assertThrows(NullPointerException.class, () -> repository.save(null));
        assertEquals(1L, repository.save(new Book("Книга", "Автор", 2000)));
    }
}
