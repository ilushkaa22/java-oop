/** Менеджер с премией 15% и отчётом о подчинённых. */
public final class Manager extends Employee implements Reportable {
    private static final double MANAGER_BONUS_RATE = 0.15;
    private final int subordinatesCount;

    /**
     * Создаёт менеджера.
     * @param name имя
     * @param department отдел
     * @param salary зарплата
     * @param subordinatesCount неотрицательное число подчинённых
     * @throws IllegalArgumentException если данные некорректны
     */
    public Manager(String name, String department, double salary, int subordinatesCount) {
        super(name, department, salary);
        if (subordinatesCount < 0) {
            throw new IllegalArgumentException("Число подчинённых не может быть отрицательным");
        }
        this.subordinatesCount = subordinatesCount;
    }

    /** @return число подчинённых */
    public int getSubordinatesCount() {
        return subordinatesCount;
    }

    /** @return премия менеджера: 15% зарплаты */
    @Override
    public double calculateBonus() {
        return getSalary() * MANAGER_BONUS_RATE;
    }

    /** @return описание менеджера с числом подчинённых */
    @Override
    public String toString() {
        return super.toString() + " — менеджер, подчинённых: " + subordinatesCount;
    }

    /** @return отчёт с явно обозначенной заглушкой списка подчинённых */
    @Override
    public String generateReport() {
        return "Отчёт менеджера " + getName() + ": подчинённых — " + subordinatesCount
                + "; список подчинённых: [заглушка]";
    }
}
