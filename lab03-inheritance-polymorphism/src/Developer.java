/** Разработчик с премией 10% зарплаты и дополнительным техническим бонусом. */
public final class Developer extends Employee implements Reportable {
    private static final double DEVELOPER_BONUS_RATE = 0.10;
    private final String programmingLanguage;
    private final double techBonus;

    /**
     * Создаёт разработчика.
     * @param name имя
     * @param department отдел
     * @param salary зарплата
     * @param programmingLanguage непустое название языка программирования
     * @param techBonus конечный неотрицательный дополнительный бонус
     * @throws IllegalArgumentException если данные некорректны
     */
    public Developer(String name, String department, double salary,
                     String programmingLanguage, double techBonus) {
        super(name, department, salary);
        this.programmingLanguage = requireText(programmingLanguage, "Язык программирования");
        this.techBonus = requireNonNegative(techBonus, "Технический бонус");
    }

    /** @return язык программирования */
    public String getProgrammingLanguage() {
        return programmingLanguage;
    }

    /** @return дополнительный технический бонус */
    public double getTechBonus() {
        return techBonus;
    }

    /** @return премия: 10% зарплаты плюс технический бонус */
    @Override
    public double calculateBonus() {
        return getSalary() * DEVELOPER_BONUS_RATE + techBonus;
    }

    /** @return описание разработчика с языком программирования */
    @Override
    public String toString() {
        return super.toString() + " — разработчик, язык: " + programmingLanguage;
    }

    /** @return отчёт с явно обозначенной заглушкой списка задач */
    @Override
    public String generateReport() {
        return "Отчёт разработчика " + getName() + ": завершённые задачи: [заглушка]";
    }
}
