import java.util.Arrays;

/** Демонстрация пары, обмена элементов и обобщённого хранилища. */
public final class Main {
    private Main() { }

    /**
     * Запускает демонстрацию.
     * @param args аргументы не используются
     */
    public static void main(String[] args) {
        demonstratePair();
        demonstrateSwap();
        demonstrateRepository();
    }

    private static void demonstratePair() {
        System.out.println("=== Pair ===");
        Pair<Integer, Integer> numbers = new Pair<>(20, 10);
        System.out.println("first = " + numbers.getFirst());
        System.out.println("second = " + numbers.getSecond());
        System.out.println("min = " + numbers.min());
        System.out.println("max = " + numbers.max());
        Pair<String, String> words = new Pair<>("Java", "Algorithms");
        System.out.println("Строки: min = " + words.min() + ", max = " + words.max());
    }

    private static void demonstrateSwap() {
        System.out.println("\n=== swap ===");
        Integer[] numbers = {10, 20, 30};
        System.out.println("До: " + Arrays.toString(numbers));
        Pair.swap(numbers, 0, 2);
        System.out.println("После: " + Arrays.toString(numbers));
        String[] words = {"Java", "SQL"};
        Pair.swap(words, 0, 1);
        System.out.println("Строки после обмена: " + Arrays.toString(words));
    }

    private static void demonstrateRepository() {
        System.out.println("\n=== Repository<String> ===");
        Repository<String> repository = new Repository<>();
        long firstId = repository.save("Java");
        long secondId = repository.save("SQL");
        System.out.println("ID: " + firstId + ", " + secondId);
        System.out.println("Все элементы: " + repository.findAll());
        System.out.println("Поиск ID " + firstId + ": " + repository.findById(firstId));
        System.out.println("Удаление ID " + secondId + ": " + repository.delete(secondId));
        System.out.println("Повторное удаление: " + repository.delete(secondId));
        System.out.println("Поиск удалённого: " + repository.findById(secondId));
        System.out.println("Осталось: " + repository.findAll());
    }
}
