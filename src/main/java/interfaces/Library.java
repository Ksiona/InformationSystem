package interfaces;

import java.util.Collection;
import java.util.List;

public interface Library {
	public List<RecordsList> getRecordsLists();
	public Collection<Record> getAllRecords();
	public RecordsList getRecordsList(String recordListName);
	public Record getRecord(String recordTitle);
	public void setRecord(String recordTitle, Record newRecord);
	public void insertRecord(Record newRecord);
	public void removeRecord(String RecordsListName, Record record);
	public void removeRecordsList(String recordListName);
	public void insertRecordsList(String recordsListName, Collection<Record> newGenreTracks);
	public boolean checkExist(String genreName);
}
