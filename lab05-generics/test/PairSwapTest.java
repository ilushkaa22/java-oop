import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PairSwapTest {
    @Test
    void swapsDifferentReferenceTypes() {
        String[] titles = {"Java", "Algorithms", "SQL"};
        Pair.swap(titles, 0, 2);
        assertArrayEquals(new String[] {"SQL", "Algorithms", "Java"}, titles);
        Integer[] numbers = {1, 2};
        Pair.swap(numbers, 0, 1);
        assertArrayEquals(new Integer[] {2, 1}, numbers);
    }

    @Test
    void supportsSameIndexAndNullElements() {
        String[] array = {"Java", null};
        Pair.swap(array, 0, 0);
        assertArrayEquals(new String[] {"Java", null}, array);
        Pair.swap(array, 0, 1);
        assertArrayEquals(new String[] {null, "Java"}, array);
    }

    @Test
    void rejectsInvalidIndicesWithoutChangingArray() {
        Integer[] array = {1, 2};
        int[][] invalid = {{-1, 0}, {0, -1}, {2, 0}, {0, 2}};
        for (int[] indices : invalid) {
            assertThrows(IndexOutOfBoundsException.class,
                    () -> Pair.swap(array, indices[0], indices[1]));
            assertArrayEquals(new Integer[] {1, 2}, array);
        }
        assertThrows(IndexOutOfBoundsException.class,
                () -> Pair.swap(new String[0], 0, 0));
    }

    @Test
    void rejectsNullArray() {
        assertThrows(NullPointerException.class, () -> Pair.swap(null, 0, 0));
    }
}
