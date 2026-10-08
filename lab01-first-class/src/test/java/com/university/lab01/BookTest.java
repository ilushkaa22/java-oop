package com.university.lab01;

import org.junit.jupiter.api.Test;

import java.time.Year;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookTest {
    @Test
    void constructorAndGettersStoreValidValues() {
        Book book = new Book(1, "Война и мир", "Толстой Л.Н.", 1869);

        assertEquals(1, book.getId());
        assertEquals("Война и мир", book.getTitle());
        assertEquals("Толстой Л.Н.", book.getAuthor());
        assertEquals(1869, book.getYear());
    }

    @Test
    void settersUpdateBook() {
        Book book = new Book(1, "Название", "Автор", 2000);

        book.setId(2);
        book.setTitle("Новое название");
        book.setAuthor("Новый автор");
        book.setYear(2020);

        assertEquals(2, book.getId());
        assertEquals("Новое название", book.getTitle());
        assertEquals("Новый автор", book.getAuthor());
        assertEquals(2020, book.getYear());
    }

    @Test
    void descriptionHasRequiredFormat() {
        Book book = new Book(1, "Война и мир", "Толстой Л.Н.", 1869);

        String expected = "\"Война и мир\" — Толстой Л.Н. (1869)";
        assertEquals(expected, book.getDescription());
        assertEquals(expected, book.toString());
    }

    @Test
    void idMustBePositive() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(0, "Название", "Автор", 2000));
    }

    @Test
    void titleMustNotBeNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, null, "Автор", 2000));
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "   ", "Автор", 2000));
    }

    @Test
    void authorMustNotBeNullOrBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "Название", null, 2000));
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "Название", "   ", 2000));
    }

    @Test
    void yearMustBeInAllowedRange() {
        int nextYear = Year.now().getValue() + 1;

        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "Название", "Автор", -1));
        assertThrows(IllegalArgumentException.class,
                () -> new Book(1, "Название", "Автор", nextYear));
    }
}
