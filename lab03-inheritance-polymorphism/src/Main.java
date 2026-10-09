import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Демонстрирует наследование и полиморфизм в лабораторной работе №3. */
public final class Main {
    private Main() {
    }

    /**
     * Показывает премии, отчёты и способы чтения книг.
     * @param args аргументы командной строки, не используются
     */
    public static void main(String[] args) {
        Employee[] employees = {
                new Manager("Иванов", "IT", 150000, 5),
                new Developer("Петров", "IT", 120000, "Java", 3000),
                new Employee("Сидоров", "Библиотека", 60000),
                new Manager("Смирнова", "Библиотека", 90000, 3),
                new Developer("Кузнецова", "IT", 100000, "Python", 5000)
        };
        List<Reportable> reporters = new ArrayList<>();
        printBonuses(employees, reporters);
        System.out.println("\n=== Интерфейс Reportable ===");
        printReports(reporters);
        demonstrateBooks(reporters);
    }

    private static void printBonuses(Employee[] employees, List<Reportable> reporters) {
        System.out.println("=== Полиморфизм ===");
        for (Employee employee : employees) {
            System.out.printf(Locale.ROOT, "%s — бонус: %.2f руб.%n",
                    employee, employee.calculateBonus());
            if (employee instanceof Reportable reporter) {
                reporters.add(reporter);
            }
        }
    }

    private static void demonstrateBooks(List<Reportable> reporters) {
        LibraryBook[] books = {
                new PrintedBook(1, "Война и мир", "Толстой Л.Н.", 1869),
                new EBook(2, "Мастер и Маргарита", "Булгаков М.А.", 1967)
        };
        System.out.println("\n=== Книги: абстрактный класс ===");
        for (LibraryBook book : books) {
            System.out.println(book.getDescription() + "; " + book.read());
            reporters.add(book);
        }
        System.out.println("Создано книг: " + Book.getCounter());
        System.out.println("\n=== Общий список Reportable: сотрудники и книги ===");
        printReports(reporters);
    }

    private static void printReports(List<Reportable> reporters) {
        for (Reportable reporter : reporters) {
            System.out.println(reporter.generateReport());
        }
    }
}
