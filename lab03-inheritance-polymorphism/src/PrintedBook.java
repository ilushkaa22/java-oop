/** Печатная библиотечная книга. */
public final class PrintedBook extends LibraryBook {
    /**
     * Создаёт печатную книгу.
     * @param id идентификатор
     * @param title название
     * @param author автор
     * @param year год издания
     */
    public PrintedBook(long id, String title, String author, int year) {
        super(id, title, author, year);
    }

    /** @return полное описание с видом книги */
    @Override
    public String getDescription() {
        return super.getDescription() + " — печатная книга";
    }

    /** @return способ чтения печатной книги */
    @Override
    public String read() {
        return "Читать бумажные страницы";
    }
}
