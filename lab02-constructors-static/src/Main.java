/** Демонстрирует конструкторы, фабричный метод и static в лабораторной №2. */
public final class Main {
    private Main() {
    }

    /**
     * Создаёт книги всеми предусмотренными способами и выводит статистику.
     * @param args аргументы командной строки, не используются
     */
    public static void main(String[] args) {
        Book defaultBook = new Book();
        Book fullBook = new Book(100, "Война и мир", "Толстой Л.Н.", 1869);
        Book bookWithoutId = new Book("Преступление и наказание", "Достоевский Ф.М.", 1866);
        Book bookWithoutYear = new Book("Евгений Онегин", "Пушкин А.С.");
        Book factoryBook = Book.createBook("Мастер и Маргарита", "Булгаков М.А.", 1967);

        System.out.println("=== Создание книг ===");
        printBook("Без параметров", defaultBook);
        printBook("Со всеми параметрами", fullBook);
        printBook("Без id", bookWithoutId);
        printBook("Только название и автор", bookWithoutYear);
        printBook("Через фабричный метод", factoryBook);
        System.out.println("Краткое описание: " + bookWithoutId.getDescription(true));
        System.out.println("Полное описание: " + bookWithoutId.getDescription(false));
        System.out.println();
        System.out.println("=== Статистика ===");
        System.out.println("Всего создано книг: " + Book.getCounter());
    }

    private static void printBook(String label, Book book) {
        System.out.printf("%s: id=%d, %s%n", label, book.getId(), book.getDescription());
    }
}
