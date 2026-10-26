import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Хранилище в памяти с собственными последовательными ключами, без потокобезопасности.
 * @param <T> тип сохраняемых объектов
 */
public final class Repository<T> {
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
        if (entity == null) {
            throw new NullPointerException("Объект репозитория не должен быть пустой");
        }
        long id = nextId++;
        storage.put(id, entity);
        return id;
    }

    /**
     * Находит объект по ключу хранилища.
     * @param id ключ
     * @return объект или null, если ключ отсутствует
     */
    public T findById(long id) {
        return storage.get(id);
    }

    /**
     * Удаляет объект по ключу.
     * @param id ключ
     * @return true, если объект существовал и был удалён
     */
    public boolean delete(long id) {
        return storage.remove(id) != null;
    }

    /**
     * Возвращает отдельный изменяемый список в порядке сохранения.
     * @return поверхностная копия значений; сами объекты не копируются
     */
    public List<T> findAll() {
        return new ArrayList<>(storage.values());
    }
}
