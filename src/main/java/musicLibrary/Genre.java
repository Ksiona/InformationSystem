package musicLibrary;

import interfaces.Record;
import interfaces.RecordsList;

import java.io.Serializable;
import java.util.Collection;

/**
 * Представляет собой список записей одного и того же жанра жанра в музыкальной бибилиотеке
 */
public class Genre implements Serializable, RecordsList{

	private static final long serialVersionUID = 1L;
    private Collection<Record> tracks;
	private String genreName;

    /**
     * Содает новый список по жанру
     * @param genreName название жанра
     * @param tracks коллекция треков
     */
	public Genre(String genreName, Collection<Record> tracks) {
		this.tracks = tracks;
		this.genreName = genreName;
	}

    /**
     * Создает новый список по жанру
     * @param genreName название жанра
     */
	public Genre(String genreName) {
		this.genreName = genreName;
	}
	
	@Override
	public Collection<Record> getRecords(){
		return tracks;
	}
	
	@Override
	public Record getRecord(String trackTitle){
		Record track = null;
    	for(Record currentTrack:tracks)
    		if(currentTrack.getTrackTitle().equalsIgnoreCase(trackTitle)){
    			track = currentTrack;
    			break;
    		}
    	return track;
    }
	
	@Override
	public void setRecord(String trackTitle, Record newTrack){
		for(Record track:tracks)
			if(track.getTrackTitle().equalsIgnoreCase(trackTitle)){
				track.setGenre(newTrack.getGenre());
				track.setSinger(newTrack.getSinger());
				track.setAlbum(newTrack.getAlbum());
				track.setRecordLength(newTrack.getRecordLength());
				break;
			}
	}
	
	@Override
	public void insertRecord(Record newTrack){
		tracks.add(newTrack);
	}
	
	@Override
	public void removeRecord(Record track){
		tracks.remove(track);
	}

	@Override
	public String getRecordsListName() {
		return genreName;
	}

	@Override
	public void setRecordsListName(String genreName) {
		this.genreName = genreName;
	}

}
