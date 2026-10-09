# Диаграмма классов

Схема подготовлена до реализации исходников. Иерархия сотрудников выполняет обязательное задание; иерархия книг сохраняет предметную область предыдущих работ и демонстрирует абстрактный метод. Обе используют независимый контракт Reportable.

```mermaid
classDiagram
    Employee <|-- Manager
    Employee <|-- Developer
    Reportable <|.. Manager
    Reportable <|.. Developer
    Book <|-- LibraryBook
    LibraryBook <|-- PrintedBook
    LibraryBook <|-- EBook
    Reportable <|.. LibraryBook
    class Employee {
        -String name
        -String department
        -double salary
        +calculateBonus() double
    }
    class Manager {
        -int subordinatesCount
        +calculateBonus() double
        +generateReport() String
    }
    class Developer {
        -String programmingLanguage
        -double techBonus
        +calculateBonus() double
        +generateReport() String
    }
    class Reportable {
        <<interface>>
        +generateReport() String
    }
    class LibraryBook {
        <<abstract>>
        +read() String*
        +generateReport() String
    }
    class PrintedBook {
        +read() String
        +getDescription() String
    }
    class EBook {
        +read() String
        +getDescription() String
    }
```
