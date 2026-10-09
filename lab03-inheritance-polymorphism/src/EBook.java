/** Электронная библиотечная книга. */
public final class EBook extends LibraryBook {
    /**
     * Создаёт электронную книгу.
     * @param id идентификатор
     * @param title название
     * @param author автор
     * @param year год издания
     */
    public EBook(long id, String title, String author, int year) {
        super(id, title, author, year);
    }

    /** @return полное описание с видом книги */
    @Override
    public String getDescription() {
        return super.getDescription() + " — электронная книга";
    }

    /** @return способ чтения электронной книги */
    @Override
    public String read() {
        return "Читать на электронной читалке";
    }
}
