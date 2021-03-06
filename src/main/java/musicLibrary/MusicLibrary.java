package musicLibrary;

import interfaces.Library;
import interfaces.Listener;
import interfaces.Record;
import interfaces.RecordsList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * <p>Представляет собой модель музыкальной библиотеки, состоящий из нескольких списков треков, каталогизированных по принципу один жанр - один список.</p>
 * <p>Имеет список объектов-слушателей, которым отсылает сообщения об успешном совершении изменений.</p>
 */
public class MusicLibrary implements Library{
	private static final String WARNING_NO_CORRECT_VALUE = " - wrong value, or you forgot to use quotation marks";
	private static final Object STATUS_SUCCESSFUL = "Operation successful";
	private List<RecordsList> genres;
    private Collection<Listener> listeners;

    /**
     * Создает новую модель библиотеки
     * @param genres список треков по жанрам
     */
	public MusicLibrary(List<RecordsList> genres) {
		this.genres = genres;
        this.listeners = new ArrayList<Listener>();
	}
	
	public List<RecordsList> getRecordsLists(){
		return genres;
    }

    /**
     * Добавляет объект-слушатель в список
     * @param listener объект-слушатель
     */
    public void AddListener(Listener listener)
    {
        this.listeners.add(listener);
    }

    /**
     * Уведомляет каждого слушателя о неком событии
     * @param arg объект, передаваемы слушателю
     */
    private void notifyListeners(Object arg)
    {
        for(Listener listener: listeners)
        {
            listener.doEvent(arg);
        }
    }

    @Override
	public Collection<Record> getAllRecords(){
		 Collection<Record> allTracks = new HashSet<>();
	    	for(RecordsList genre:genres){
	    		allTracks.addAll(genre.getRecords());
	    	}
			return allTracks;
	 }
	
	@Override 
    public RecordsList getRecordsList(String genreName){
    	RecordsList genre = null;
    	for (RecordsList currentGenre:genres){
    		if(currentGenre.getRecordsListName().equalsIgnoreCase(genreName)){
    			genre = currentGenre;
    			break;
    		}
    	}
    	if (genre == null)
    		throw new IllegalArgumentException(genreName + WARNING_NO_CORRECT_VALUE);
		return genre;
    }
    
	@Override
    public Record getRecord(String trackTitle){
    	Record track = null;
    	for(RecordsList genre:genres)
    		for(Record currentTrack:genre.getRecords())
    			if(currentTrack.getTrackTitle().equalsIgnoreCase(trackTitle)){
    				track = currentTrack;
    				break;
    			}
    	if (track == null)
    		throw new IllegalArgumentException(trackTitle + WARNING_NO_CORRECT_VALUE);
    	return track;
    }

    /**
     * Изменяет все параметры(за исключением названия) записи с данным названием(если таковая имеется) на параметры новой.<br>
     * Уведомляет слушателей об успешном заверщении операции
     */
	@Override
	public void setRecord(String trackTitle, Record newTrack){
		Record track = getRecord(trackTitle);
		RecordsList genre = getRecordsList(track.getGenre());
		genre.setRecord(trackTitle, newTrack);
        this.notifyListeners(STATUS_SUCCESSFUL);
	}

    /**
     * Добавляет новую запись в соответствующий ей список, если такого не существует - создает его.<br>
     * Уведомляет слушателей об успешном заверщении операции/
     */
	@Override
	public void insertRecord(Record newTrack){
		boolean added = false;
		for(RecordsList genre:genres)
			if(genre.getRecordsListName().equalsIgnoreCase(newTrack.getGenre())){
				genre.insertRecord(newTrack);
				added = true;
				break;
			}
		if(!added){
			Collection<Record> newGenreTracks = new HashSet<>();
			newGenreTracks.add(newTrack);
			genres.add(new Genre(newTrack.getGenre(), newGenreTracks));
		}
        this.notifyListeners(STATUS_SUCCESSFUL);
	}

    /**
     * Удаляет конкретную запись из конкретного списка
     * Уведомляет слушателей об успешном завершении
     */
	@Override
	public void removeRecord(String genreName, Record currentTrack) {
		getRecordsList(genreName).removeRecord(currentTrack);
        this.notifyListeners(STATUS_SUCCESSFUL);
	}
	
	@Override
	public void removeRecordsList(String genreName) {
		genres.remove(getRecordsList(genreName));
	}
	
	public boolean checkExist(String newGenreName){
		for(RecordsList genre:genres)
			if(genre.getRecordsListName().equalsIgnoreCase(newGenreName))
				return true;
		return false;
	}
	
	public void insertRecordsList(String newGenreName, Collection<Record> newGenreTracks){
			if(!checkExist(newGenreName))
				genres.add(new Genre(newGenreName, newGenreTracks));
	}
}
