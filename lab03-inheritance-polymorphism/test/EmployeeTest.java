import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EmployeeTest {
    private static final double DELTA = 0.000001;

    @Test
    void employeeStoresFieldsAndCalculatesBaseBonus() {
        Employee employee = new Employee("Сидоров", "Библиотека", 60000);
        assertEquals("Сидоров", employee.getName());
        assertEquals("Библиотека", employee.getDepartment());
        assertEquals(60000, employee.getSalary(), DELTA);
        assertEquals(3000, employee.calculateBonus(), DELTA);
        assertEquals("Сидоров (Библиотека)", employee.toString());
    }

    @Test
    void arrayOfBaseTypeDispatchesToActualImplementations() {
        Employee[] employees = {
                new Employee("А", "Отдел", 100000),
                new Manager("Б", "Отдел", 100000, 5),
                new Developer("В", "Отдел", 100000, "Java", 3000)
        };
        double[] expectedBonuses = {5000, 15000, 13000};
        for (int index = 0; index < employees.length; index++) {
            assertEquals(expectedBonuses[index], employees[index].calculateBonus(), DELTA);
        }
    }

    @Test
    void subclassesPreserveBaseDescriptionAndAdditionalFields() {
        Manager manager = new Manager("Иванов", "IT", 150000, 5);
        Developer developer = new Developer("Петров", "IT", 120000, "Java", 3000);
        assertEquals(5, manager.getSubordinatesCount());
        assertEquals("Java", developer.getProgrammingLanguage());
        assertEquals(3000, developer.getTechBonus(), DELTA);
        assertEquals("Иванов (IT) — менеджер, подчинённых: 5", manager.toString());
        assertEquals("Петров (IT) — разработчик, язык: Java", developer.toString());
    }

    @Test
    void reportsAreAvailableThroughInterfaceAndMarkPlaceholders() {
        List<Reportable> reporters = List.of(
                new Manager("Иванов", "IT", 150000, 5),
                new Developer("Петров", "IT", 120000, "Java", 3000));
        assertEquals("Отчёт менеджера Иванов: подчинённых — 5; список подчинённых: [заглушка]",
                reporters.get(0).generateReport());
        assertEquals("Отчёт разработчика Петров: завершённые задачи: [заглушка]",
                reporters.get(1).generateReport());
        assertTrue(reporters.stream().allMatch(reporter -> reporter.generateReport().contains("[заглушка]")));
    }

    @Test
    void zeroValuesAreAccepted() {
        assertEquals(0, new Employee("Имя", "Отдел", 0).calculateBonus(), DELTA);
        assertEquals(0, new Manager("Имя", "Отдел", 0, 0).calculateBonus(), DELTA);
        assertEquals(0, new Developer("Имя", "Отдел", 0, "Java", 0).calculateBonus(), DELTA);
    }

    @Test
    void emptyTextIsRejectedInBaseClassAndSubclass() {
        for (String text : new String[]{null, "", "  "}) {
            assertThrows(IllegalArgumentException.class, () -> new Employee(text, "Отдел", 1));
            assertThrows(IllegalArgumentException.class, () -> new Employee("Имя", text, 1));
            assertThrows(IllegalArgumentException.class,
                    () -> new Developer("Имя", "Отдел", 1, text, 0));
        }
    }

    @Test
    void negativeAndNonFiniteMoneyIsRejected() {
        for (double value : new double[]{-1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY}) {
            assertThrows(IllegalArgumentException.class, () -> new Employee("Имя", "Отдел", value));
            assertThrows(IllegalArgumentException.class,
                    () -> new Developer("Имя", "Отдел", 1, "Java", value));
        }
    }

    @Test
    void negativeSubordinatesCountIsRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Manager("Имя", "Отдел", 1, -1));
    }
}
