/** Абстрактная библиотечная книга с общим отчётом и способом чтения. */
public abstract class LibraryBook extends Book implements Reportable {
    /**
     * Инициализирует поля через конструктор Book из второй лабораторной.
     * @param id идентификатор
     * @param title название
     * @param author автор
     * @param year год издания
     */
    protected LibraryBook(long id, String title, String author, int year) {
        super(id, title, author, year);
    }

    /** @return способ чтения, определяемый конкретным видом книги */
    public abstract String read();

    /** @return описание книги и способ чтения */
    @Override
    public String generateReport() {
        return getDescription() + "; " + read();
    }
}
