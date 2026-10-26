import java.util.List;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class RepositoryTest {
    @Test
    void startsEmptyAndHandlesMissingKeys() {
        Repository<String> repository = new Repository<>();
        assertTrue(repository.findAll().isEmpty());
        for (long id : new long[] {-1, 0, 1, Long.MAX_VALUE}) {
            assertNull(repository.findById(id));
            assertFalse(repository.delete(id));
        }
    }

    @Test
    void assignsKeyAndFindsValue() {
        Repository<String> repository = new Repository<>();
        long key = repository.save("Java");
        assertEquals(1, key);
        assertEquals("Java", repository.findById(key));
    }

    @Test
    void keepsInsertionOrderAndDoesNotDeduplicate() {
        Repository<String> repository = new Repository<>();
        assertEquals(1, repository.save("B"));
        assertEquals(2, repository.save("A"));
        assertEquals(3, repository.save("B"));
        assertEquals(List.of("B", "A", "B"), repository.findAll());
    }

    @Test
    void deletesOnceAndDoesNotReuseKeys() {
        Repository<String> repository = new Repository<>();
        long key = repository.save("A");
        assertTrue(repository.delete(key));
        assertNull(repository.findById(key));
        assertFalse(repository.delete(key));
        assertEquals(2, repository.save("B"));
        assertEquals(List.of("B"), repository.findAll());
    }

    @Test
    void returnedListIsIndependentOfStorage() {
        Repository<String> repository = new Repository<>();
        repository.save("A");
        List<String> snapshot = repository.findAll();
        repository.save("B");
        assertEquals(List.of("A"), snapshot);
        snapshot.clear();
        assertEquals(List.of("A", "B"), repository.findAll());
    }

    @Test
    void rejectsNullWithoutConsumingKey() {
        Repository<String> repository = new Repository<>();
        assertThrows(NullPointerException.class, () -> repository.save(null));
        assertTrue(repository.findAll().isEmpty());
        assertEquals(1, repository.save("A"));
    }

    @Test
    void repositoriesHaveIndependentCounters() {
        Repository<String> first = new Repository<>();
        Repository<Integer> second = new Repository<>();
        assertEquals(1, first.save("A"));
        assertEquals(1, second.save(42));
        assertEquals(42, second.findById(1));
    }
}
