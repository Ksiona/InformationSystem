package output;

import interfaces.Record;

import java.util.Collection;

/**
 * Created by Morthanion on 14.11.2014.
 */

/**
 * Контейнер для списка записей
 */
public class ListContainer {
    private Collection<Record> collection;

    /**
     * Возвращет коллекцию записей
     * @return коллекция записей
     */
    public Collection<Record> getCollection() {
        return collection;
    }

    /**
     * Создает новый экземпляр класса
     * @param collection коллекция записей
     */
    public ListContainer(Collection<Record> collection){
        this.collection = collection;
    }
}
