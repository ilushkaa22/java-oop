package com.university.lab01;

import java.time.Year;

/**
 * Представляет книгу с идентификатором, названием, автором и годом издания.
 */
public class Book {
    private long id;
    private String title;
    private String author;
    private int year;

    /**
     * Создаёт книгу и проверяет переданные значения через сеттеры.
     *
     * @param id уникальный положительный идентификатор
     * @param title название книги
     * @param author автор книги
     * @param year год издания от 0 до текущего года
     */
    public Book(long id, String title, String author, int year) {
        setId(id);
        setTitle(title);
        setAuthor(author);
        setYear(year);
    }

    /**
     * Возвращает идентификатор книги.
     *
     * @return идентификатор книги
     */
    public long getId() {
        return id;
    }

    /**
     * Устанавливает идентификатор книги.
     *
     * @param id положительный идентификатор
     * @throws IllegalArgumentException если идентификатор неположительный
     */
    public void setId(long id) {
        if (id <= 0) {
            throw new IllegalArgumentException("Идентификатор должен быть положительным");
        }
        this.id = id;
    }

    /**
     * Возвращает название книги.
     *
     * @return название книги
     */
    public String getTitle() {
        return title;
    }

    /**
     * Устанавливает название книги.
     *
     * @param title непустое название
     * @throws IllegalArgumentException если название равно {@code null} или пусто
     */
    public void setTitle(String title) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Название не может быть пустым");
        }
        this.title = title;
    }

    /**
     * Возвращает автора книги.
     *
     * @return автор книги
     */
    public String getAuthor() {
        return author;
    }

    /**
     * Устанавливает автора книги.
     *
     * @param author непустое имя автора
     * @throws IllegalArgumentException если автор равен {@code null} или пуст
     */
    public void setAuthor(String author) {
        if (author == null || author.isBlank()) {
            throw new IllegalArgumentException("Автор не может быть пустым");
        }
        this.author = author;
    }

    /**
     * Возвращает год издания книги.
     *
     * @return год издания
     */
    public int getYear() {
        return year;
    }

    /**
     * Устанавливает год издания книги.
     *
     * @param year год от 0 до текущего включительно
     * @throws IllegalArgumentException если год находится вне допустимого диапазона
     */
    public void setYear(int year) {
        int currentYear = Year.now().getValue();
        if (year < 0 || year > currentYear) {
            throw new IllegalArgumentException("Год должен быть в диапазоне от 0 до " + currentYear);
        }
        this.year = year;
    }

    /**
     * Формирует краткое описание книги.
     *
     * @return описание в формате «Название» — Автор (год)
     */
    public String getDescription() {
        return "\"%s\" — %s (%d)".formatted(title, author, year);
    }

    /**
     * Возвращает строковое представление книги.
     *
     * @return краткое описание книги
     */
    @Override
    public String toString() {
        return getDescription();
    }
}
