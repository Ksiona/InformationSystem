package interfaces;

import java.util.Collection;
import java.util.List;

/**
 * Интерфейс музыкальной бибилиотеки
 */
public interface Library {
    /**
     * Возвращает список содежращий все списки записей
     * @return список со всем списками записей
     */
	public List<RecordsList> getRecordsLists();

    /**
     * Возвращает коллекцию со всеми записями в бибилиотеке
     * @return коллекция с записями
     *
     */
	public Collection<Record> getAllRecords();

    /**
     * Возвращает список с определенным названием
     * @param recordListName название нужного списка
     * @return список с заданым названием
     * @exception java.lang.IllegalArgumentException если списка с нужным названием не найдено
     */
	public RecordsList getRecordsList(String recordListName);

    /**
     * Возвращает запись с определенным названием
     * @param recordTitle название нужной записи
     * @return запись с нужным названием
     * @exception java.lang.IllegalArgumentException если записи с нужным названием не найдено
     */
	public Record getRecord(String recordTitle);

    /**
     * Изменяет все параметры(за исключением названия) записи с данным названием(если таковая имеется) на параметры новой
     * @param recordTitle название нужной записи
     * @param newRecord новая запись
     */
	public void setRecord(String recordTitle, Record newRecord);

    /**
     * Добавляет новую запись в соответствующий ей список, если такого не существует - создает его.
     * @param newRecord новая запись
     */
	public void insertRecord(Record newRecord);

    /**
     * Удаляет конкретную запись из конкретного списка
     * @param RecordsListName название списка в котором содержится нужная запись
     * @param record название записи на удаление
     */
	public void removeRecord(String RecordsListName, Record record);

    /**
     * Удаляет конкретный список записи
     * @param recordListName название списка на удаление
     */
	public void removeRecordsList(String recordListName);

    /**
     * Добавляет новый список записией, если такового не существует
     * @param recordsListName название нового списка
     * @param newGenreTracks коллекци записей, которая должна содержаться в нем
     */
	public void insertRecordsList(String recordsListName, Collection<Record> newGenreTracks);

    /**
     * Проверяет, существует ли список с заданным названием
     * @param genreName название нужного списка
     * @return true, если нужный список сущевствует, false - иначе
     */
	public boolean checkExist(String genreName);
}
