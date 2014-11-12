package management;

import interfaces.Library;
import interfaces.Listener;
import interfaces.Record;
import interfaces.RecordsList;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import musicLibrary.Genre;
import musicLibrary.MusicLibrary;
import musicLibrary.Track;

import org.apache.log4j.Logger;

import output.DisplaySystem;

public class ManagementSystem implements Listener {
	
	private static final String STORAGE = "Storage/";
	private static final String UNSORTED_RECORDSLIST_NAME = "Unsorted";
	private static final String FILE_EXTENSION = ".bin";
	private static final String DOT = ".";
	private static final String STATUS_REMOVING ="Removing track...";
	private static final String STATUS_SETTING = "Set changes...";
	private static final String STATUS_INSERTING = "Inserting track...";
	private static final String STATUS_WRITING_TO_FILE_SUCCESS = "Successfully updated storage: ";
	private static final String STATUS_REMOVE_FILE_SUCCESS = "Successfully remove storage: ";
	private static final String FILE_HAS_BEEN_DELETED = " file has been deleted";
	private static final String WARNING_GENRE_ALREADY_EXIST = " genre already exist";
	private static final String WARNING_WRONG_PARAMETER = "Operation aborted, check the passed parameters, please";
	
	private static DisplaySystem ds;
    private static final Logger log = Logger.getLogger(ManagementSystem.class);
	private Library musicLibrary;
    private  List<String> genreFilesDuplicates = new ArrayList<>();;

	private ManagementSystem(){
        ManagementSystem.ds = DisplaySystem.getInstance();
        MusicLibrary library = new MusicLibrary(loadGenres(STORAGE));
        library.AddListener(this);
        this.musicLibrary = library;
    }

	private static class SingletonHolder {
		private static final ManagementSystem INSTANCE = new ManagementSystem();
	}
	
	public static ManagementSystem getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
    public List<RecordsList> loadGenres(String storage){
    	List<RecordsList> genres = new ArrayList<>();
    	Collection<Record> tracks = new HashSet<>();
    	boolean inserted;
    	try {
			File dir = new File(storage.concat(DOT));
			for(File file : dir.listFiles())
				if((file.getName().endsWith(FILE_EXTENSION))&& file.isFile()){
					inserted =false;
					tracks = (HashSet)deserialize(storage + file.getName(), HashSet.class);
					if (!tracks.isEmpty()){
						String genreName = tracks.iterator().next().getGenre();
						for(RecordsList genre:genres)
							if(genre.getRecordsListName().equals(genreName)){
								genre.getRecords().addAll(tracks);
								genreFilesDuplicates.add(genreName);
								inserted =true;
							}
						if(!inserted){
							genres.add(new Genre(genreName, tracks));
						}
					}
				}
		} catch (ClassNotFoundException | IOException e) {
			ds.DisplayError(e);
			log.error(e.getMessage(), e);
		} 
		return genres;
    }
    
	public void writeUnsavedChanges() {
		if(!genreFilesDuplicates.isEmpty()){
			for(String genreName:genreFilesDuplicates)
				serialize(genreName);
			
			File dir = new File(STORAGE.concat(DOT));
		    List<String> genresNameList = new ArrayList<>();
		    for (RecordsList genre: musicLibrary.getRecordsLists())
		    	genresNameList.add(genre.getRecordsListName());
		   
		    List<String> filesNameList = new ArrayList<>();
		    for(String path : dir.list())
		    	if(new File(STORAGE + path).isFile())
		    		filesNameList.add(path.substring(0, path.lastIndexOf(DOT)));
	
		    boolean match;
		    for ( Iterator<String> files = filesNameList.iterator(); files.hasNext(); ) {
		    	match = false;
		    	String fileName = (String) files.next();
		    	for ( Iterator<String> names = genresNameList.iterator(); names.hasNext(); ) {
		    		String genreName = (String)names.next();
		    		if ( genreName.equals(fileName)) {
		    			match = true;
		    		}
		    	}
		    	if ( !match ) {
		    		new File(STORAGE+fileName+FILE_EXTENSION).deleteOnExit();
		    		log.info(STORAGE+fileName+FILE_EXTENSION + FILE_HAS_BEEN_DELETED);
		    	}
		    }
		}
	}

	public void printAllTracksTitle(){
    	ds.DisplayList(musicLibrary.getAllRecords());
    }

	public void getTracksTitles(String genreName){
    	try{
    		ds.DisplayList(musicLibrary.getRecordsList(genreName).getRecords());
    	} catch (IllegalArgumentException e){
    		ds.DisplayError(e);
    	}
    }
    
    public void moveRecordAnotherSet(String trackTitle, String genreName){
    	try{
    		Record currentTrack = musicLibrary.getRecord(trackTitle);
        	String oldGenre = currentTrack.getGenre();
        	currentTrack.setGenre(genreName);
        	musicLibrary.insertRecord(currentTrack);
        	musicLibrary.getRecordsList(oldGenre).removeRecord(currentTrack);
        	serialize(genreName);
        	serialize(oldGenre);
    	} catch (IllegalArgumentException e){
    		ds.DisplayError(e);
    	}
    }

    public void printTrackInfo(String trackTitle){
    	try{
    		Record track = musicLibrary.getRecord(trackTitle);
    		ds.DisplayMessage(track.toString());
    	} catch (IllegalArgumentException e){
    		ds.DisplayError(e);
    	}
    }
	
	public void insertTrack(String ... args) {
		ds.DisplayMessage(STATUS_INSERTING);
		List<String> genreList = new ArrayList<>();
		for(int i=1;i<args.length; i=i+5){
			Record newTrack = new Track (args[i], args[i+1], args[i+2], args[i+3], args[i+4]);
			if (((i-1)%5)==0 && newTrack !=null){
				musicLibrary.insertRecord(newTrack);
				genreList.add(newTrack.getGenre());
			}
		}
		for (String genreName:genreList)
			serialize(genreName);
	}
	
	public void setTrack(String trackTitle, String ... args){
		ds.DisplayMessage(STATUS_SETTING);
		try {
			Record track = musicLibrary.getRecord(trackTitle);
			BeanInfo bi = Introspector.getBeanInfo(track.getClass());
			for(int i=0;i<args.length;i++)
				for (PropertyDescriptor pd: bi.getPropertyDescriptors()) {
					if (pd.getName().equalsIgnoreCase(args[i]))
						if (pd.getWriteMethod() != null) {
								pd.getWriteMethod().invoke(track, args[i+1]);	
								break;
						}
				}
			serialize(track.getGenre());
		} catch (ArrayIndexOutOfBoundsException ex) {
			throw new IllegalArgumentException(WARNING_WRONG_PARAMETER);
		} catch (IntrospectionException | IllegalAccessException | InvocationTargetException e) {
			ds.DisplayError(e);
			log.error(e);
		}
	}
	
    public void removeRecord(String trackTitle, String genreName){
    	try{
    		ds.DisplayMessage(STATUS_REMOVING);
    		Record currentTrack = musicLibrary.getRecordsList(genreName).getRecord(trackTitle);
    		musicLibrary.removeRecord(genreName, currentTrack);
			serialize(genreName);
    	} catch (IllegalArgumentException e){
    		ds.DisplayError(e);
    	}
    }

    public void insertRecordsList(String genreName) {
		Collection<Record> newGenreTracks = new HashSet<>();
		if(!musicLibrary.checkExist(genreName)){
			musicLibrary.insertRecordsList(genreName, newGenreTracks);
			serialize(genreName);
		}
		else
			ds.DisplayMessage(WARNING_GENRE_ALREADY_EXIST);
	}
    
    public void removeRecordsList(String genreName){
    	combineRecordsLists(UNSORTED_RECORDSLIST_NAME, genreName, UNSORTED_RECORDSLIST_NAME);
    	try{
			serialize(UNSORTED_RECORDSLIST_NAME);
			musicLibrary.removeRecordsList(genreName);
			ds.DisplayMessage(STATUS_REMOVE_FILE_SUCCESS + genreName);
		}catch (IllegalArgumentException e) {
			ds.DisplayError(e);
		}
    }
    
    public void moveAllRecordsAnotherSet(String genreNameFrom, String genreNameTo){
    	RecordsList genreTo = null;
    	try{
    		genreTo  = musicLibrary.getRecordsList(genreNameTo);
    	}catch (IllegalArgumentException e){
			insertRecordsList(genreNameTo);
		}try{
    		for(Record record:musicLibrary.getRecordsList(genreNameFrom).getRecords()){
        		record.setGenre(genreNameTo);
        		genreTo.insertRecord(record);
        	}
    		musicLibrary.removeRecordsList(genreNameFrom);
    		File file = new File(STORAGE+genreNameFrom+FILE_EXTENSION);
			if(file.exists()) {
				file.deleteOnExit();;
				log.info(STORAGE+file.getName()+ FILE_HAS_BEEN_DELETED);
			}
    	}catch (IllegalArgumentException e){
    		ds.DisplayError(e);
		}
    }
    
    public void combineRecordsLists(String genreName1, String genreName2, String newGenreName) {
    	if(newGenreName.equalsIgnoreCase(genreName1))
    		moveAllRecordsAnotherSet(genreName2, genreName1);
    	else if(newGenreName.equalsIgnoreCase(genreName2))
    		moveAllRecordsAnotherSet(genreName1, genreName2);
    	else {
    		moveAllRecordsAnotherSet(genreName1, newGenreName);
    		moveAllRecordsAnotherSet(genreName2, newGenreName);
    	}
		serialize(newGenreName);
	}

    public void getRecordsListsName(){
    	for (RecordsList genre: musicLibrary.getRecordsLists())
    		ds.DisplayMessage(genre.getRecordsListName());
    }
    
	public void setRecordsListName(String oldGenreName, String newGenreName) {
		ds.DisplayMessage(STATUS_SETTING);
		moveAllRecordsAnotherSet(oldGenreName, newGenreName);
		serialize(newGenreName);
	}
    
    public void searchItems(String keyField, String mask)
    {
        Collection<Record> fits = new ArrayList<Record>();
        Iterator<Record> trackIterator = musicLibrary.getAllRecords().iterator();
        while (trackIterator.hasNext())
        {
            Record checked = trackIterator.next();
            if (checked.fitsMask(keyField, mask)) fits.add(checked);
        }
        ds.DisplayList(fits);
    }
    
    private void serialize(String objName){
		try (ObjectOutputStream objectOutStream = new ObjectOutputStream(new FileOutputStream(new File(STORAGE+objName+FILE_EXTENSION)))){		
	    	Object obj = musicLibrary.getRecordsList(objName).getRecords();
			objectOutStream.writeObject(obj);
			ds.DisplayMessage(STATUS_WRITING_TO_FILE_SUCCESS + objName);
		}catch (IOException e) {
			ds.DisplayError(e);
			log.warn(e.getMessage(), e);
		}
	}
	
    private <classType> Object deserialize(String fileName, Class classType) throws IOException, ClassNotFoundException{
		Object obj = null;
		try (ObjectInputStream objectInStream = new ObjectInputStream(new FileInputStream(new File(fileName)));) {
			obj = (classType) objectInStream.readObject();
		} catch (ClassCastException e){
			log.warn(e.getMessage(), e);
		}
		return obj;
	}
    

    @Override
    public void doEvent(Object arg) {
       if (arg.getClass().equals(String.class)) ds.DisplayMessage((String)arg);
       if (arg instanceof Exception) ds.DisplayError((Exception) arg);
    }
}
