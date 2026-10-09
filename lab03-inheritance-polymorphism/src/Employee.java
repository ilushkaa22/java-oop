/** Сотрудник с базовой премией в размере пяти процентов зарплаты. */
public class Employee {
    private static final double BASE_BONUS_RATE = 0.05;

    private final String name;
    private final String department;
    private final double salary;

    /**
     * Создаёт сотрудника с проверкой исходных данных.
     * @param name непустое имя
     * @param department непустое название отдела
     * @param salary конечная неотрицательная зарплата
     * @throws IllegalArgumentException если данные некорректны
     */
    public Employee(String name, String department, double salary) {
        this.name = requireText(name, "Имя");
        this.department = requireText(department, "Отдел");
        this.salary = requireNonNegative(salary, "Зарплата");
    }

    /** @return имя сотрудника */
    public final String getName() {
        return name;
    }

    /** @return отдел сотрудника */
    public final String getDepartment() {
        return department;
    }

    /** @return зарплата сотрудника */
    public final double getSalary() {
        return salary;
    }

    /** @return базовая премия: 5% зарплаты */
    public double calculateBonus() {
        return salary * BASE_BONUS_RATE;
    }

    /** @return имя и отдел сотрудника */
    @Override
    public String toString() {
        return name + " (" + department + ")";
    }

    protected static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " не может быть пустым");
        }
        return value;
    }

    protected static double requireNonNegative(double value, String fieldName) {
        if (!Double.isFinite(value) || value < 0) {
            throw new IllegalArgumentException(fieldName + " должен быть конечным и неотрицательным");
        }
        return value;
    }
}
