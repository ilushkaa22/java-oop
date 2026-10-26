import java.util.Objects;

/**
 * Пара сравнимых значений с конструктором и getter-методами.
 * @param <A> тип первого значения, поддерживающий сравнение
 * @param <B> тип второго значения: A или его подкласс
 */
public final class Pair<A extends Comparable<A>, B extends A> {
    private final A first;
    private final B second;

    /**
     * Создаёт пару.
     * @param first первое значение
     * @param second второе значение
     * @throws NullPointerException если значение равно null
     */

    public Pair(A first, B second) {
        this.first = Objects.requireNonNull(first, "Значение пусто");
        this.second = Objects.requireNonNull(second, "Значение пусто");
    }

    /**
     * Возвращает первое значение.
     * @return первое значение
     */
    public A getFirst() {
        return first;
    }

    /**
     * Возвращает второе значение.
     * @return второе значение
     */
    public B getSecond() {
        return second;
    }

    /**
     * Возвращает меньшее значение.
     * @return минимум; при равенстве первое значение
     */
    public A min() {
        return first.compareTo(second) <= 0 ? first : second;
    }

    /**
     * Возвращает большее значение.
     * @return максимум; при равенстве первое значение
     */
    public A max() {
        return first.compareTo(second) >= 0 ? first : second;
    }

    /**
     * Меняет местами два элемента исходного массива.
     * @param <A> тип элементов
     * @param array исходный массив
     * @param i первый индекс
     * @param j второй индекс
     * @throws NullPointerException если массив равен null
     * @throws IndexOutOfBoundsException если хотя бы один индекс некорректен
     */
    public static <A> void swap(A[] array, int i, int j) {
        A temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }
}
