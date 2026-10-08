package com.university.lab01;

/**
 * Точка входа в приложение лабораторной работы №1.
 */
public final class Main {
    private Main() {
    }

    /**
     * Создаёт три книги и выводит их описания.
     *
     * @param args аргументы командной строки, не используются
     */
    public static void main(String[] args) {
        Book[] books = {
                new Book(1, "Война и мир", "Толстой Л.Н.", 1869),
                new Book(2, "Мастер и Маргарита", "Булгаков М.А.", 1967),
                new Book(3, "Преступление и наказание", "Достоевский Ф.М.", 1866)
        };

        System.out.println("=== Список книг ===");
        for (int index = 0; index < books.length; index++) {
            System.out.printf("%d. %s%n", index + 1, books[index].getDescription());
        }
    }
}
