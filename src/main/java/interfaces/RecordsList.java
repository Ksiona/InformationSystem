package interfaces;

import java.util.Collection;

/**
 * Интерфейс списка в бибилотеке
 */
public interface RecordsList {
    /**
     * Возвращает все записи в списке
     * @return коллекцию, содержащую все записи в списке
     */
	public Collection<Record> getRecords();

    /**
     * Возвращает конкретную запись из содежимого
     * @param recordTitle название нужного трека
     * @return запись с названием recordTitle, если таковая есть, null - иначе
     * @exception java.lang.IllegalArgumentException в случае названия равного null
     */
	public Record getRecord(String recordTitle);

    /**
     * Изменяет все параметры(за исключением названия) записи с данным названием(если таковая имеется) на параметры новой
     * @param recordTitle название нужной записи
     * @param newRecord новая запись
     */
	public void setRecord(String recordTitle, Record newRecord);

    /**
     * Добавляет новую запись в список
     * @param newRecord новая запись
     */
	public void insertRecord(Record newRecord);

    /**
     * Убирает запись из списка, если таковая в нем содержится
     * @param record запись на удаление
     */
	public void removeRecord(Record record);

    /**
     * Возвращает название списка
     * @return название списка
     */
	public String getRecordsListName();

    /**
     * Задает новое название списка
     * @param recordsSetName название списка
     */
	public void setRecordsListName(String recordsSetName);
}
