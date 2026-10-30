import exceptions.BookNotFoundException;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище в памяти с собственными последовательными ключами, без потокобезопасности.
 * В этой работе используется как Repository<Book>; исключение поиска предметное.
 * @param <T> тип сохраняемых объектов
 */
public final class Repository<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(Repository.class);
    private final Map<Long, T> storage = new LinkedHashMap<>();
    private long nextId = 1;

    /** Создаёт пустое хранилище. */
    public Repository() { }

    /**
     * Добавляет объект под новым ключом, даже если такой объект уже сохранён.
     * @param entity ненулевой объект
     * @return назначенный ключ
     * @throws NullPointerException если объект равен null
     * @throws ArithmeticException если диапазон счётчика ключей исчерпан
     */
    public long save(T entity) {
        Objects.requireNonNull(entity, "Объект репозитория не должен быть null");
        long id = nextId;
        nextId = Math.incrementExact(nextId);
        storage.put(id, entity);
        LOGGER.info("Объект успешно сохранён: id={}, {}", id, entity);
        return id;
    }

    /**
     * Находит объект по ключу хранилища.
     * @param id ключ
     * @return найденный объект
     * @throws BookNotFoundException если ключ отсутствует
     */
    public T findById(long id) throws BookNotFoundException {
        T entity = storage.get(id);
        if (entity == null) {
            LOGGER.warn("Попытка получить книгу с id {}", id);
            throw new BookNotFoundException("Книга с id " + id + " не найдена");
        }
        return entity;
    }

    /**
     * Удаляет объект по ключу.
     * @param id ключ
     * @return true после успешного удаления
     * @throws BookNotFoundException если ключ отсутствует
     */
    public boolean delete(long id) throws BookNotFoundException {
        T removed = storage.remove(id);
        if (removed == null) {
            LOGGER.warn("Попытка удалить книгу с id {}", id);
            throw new BookNotFoundException("Книга с id " + id + " не найдена");
        }
        LOGGER.info("Объект успешно удалён: id={}, {}", id, removed);
        return true;
    }

    /**
     * Возвращает отдельный изменяемый список в порядке сохранения.
     * @return поверхностная копия значений; сами объекты не копируются
     */
    public List<T> findAll() {
        LOGGER.debug("Получение всех объектов: количество={}", storage.size());
        return new ArrayList<>(storage.values());
    }
}
