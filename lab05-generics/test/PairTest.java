import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PairTest {
    @Test
    void exposesBothValues() {
        Pair<Integer, Integer> pair = new Pair<>(20, 10);
        assertEquals(20, pair.getFirst());
        assertEquals(10, pair.getSecond());
    }

    @Test
    void findsIntegerExtremesInBothOrders() {
        for (Pair<Integer, Integer> pair : java.util.List.of(new Pair<>(-5, 8), new Pair<>(8, -5))) {
            assertEquals(-5, pair.min());
            assertEquals(8, pair.max());
        }
    }

    @Test
    void comparesStrings() {
        Pair<String, String> pair = new Pair<>("Java", "Algorithms");
        assertEquals("Algorithms", pair.min());
        assertEquals("Java", pair.max());
    }

    @Test
    void returnsFirstObjectOnTie() {
        String first = new String("Java");
        String second = new String("Java");
        Pair<String, String> pair = new Pair<>(first, second);
        assertSame(first, pair.min());
        assertSame(first, pair.max());
    }

    @Test
    void rejectsNullValues() {
        assertThrows(NullPointerException.class, () -> new Pair<Integer, Integer>(null, 1));
        assertThrows(NullPointerException.class, () -> new Pair<Integer, Integer>(1, null));
    }
}
