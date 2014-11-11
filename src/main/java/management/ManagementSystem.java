package management;

import interfaces.*;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import musicLibrary.Genre;
import musicLibrary.MusicLibrary;
import musicLibrary.Track;
import output.DisplaySystem;
import commands.CommandProcessor;

public class ManagementSystem implements Listener {
	

	private static final String STORAGE = "STORAGE/";
	private static final String UNSORTED_RECORDSLIST_NAME = "Unsorted";
	private static final String FILE_EXTENSION = ".bin";
	private static final String DOT = ".";
	private static final String STATUS_REMOVING ="Removing track...";
	private static final String STATUS_SETTING = "Set changes...";
	private static final String STATUS_INSERTING = "Inserting track...";
	private static final String STATUS_WRITING_TO_FILE_SUCCESS = "Successfully updated storage: ";
	private static final String STATUS_REMOVE_FILE_SUCCESS = "Successfully remove storage: ";
	private static final String WELCOME_MESSAGE = "Welcome to the information system \"Music Library\" \r\n"
										+ "To get instructions on how to use enter command \"help\"";
	private static final String CONSOLE_ENCODING = "console.encoding";
	private static final String CONSOLE_ENCODING_VALUE = "Cp866";
	private static final String FILE_ENCODING = "file.encoding";
	private static final String FILE_ENCODING_VALUE = "UTF-8";
	private static final String WARNING_ENCODING = "Unsupported encoding set for console, call support ";
	private Library musicLibrary;
	private static DisplaySystem ds;
    private static ManagementSystem instance;

	public ManagementSystem(){//Singleton pattern mustn't have public methods, otherwise it could be built bei external system, that is wron, because only one instance of singleton is allowed  
        MusicLibrary library = new MusicLibrary(loadGenres(STORAGE));//it is very strange that you use ds resource before its initialisation, please take a look into loadGenres
        library.AddListener(this);
        this.musicLibrary = library; //but i like alot, that you perform initialisation of resources in constractors, that is very important
        this.ds = DisplaySystem.getInstance(); //Why DisplaySystem is not a Singlton? please remove static initialisation 
    }

    public static synchronized ManagementSystem getInstance(){ // if this class were real singleton it would not be necessary to perform synchronisation
        if (instance == null) {
            instance = new ManagementSystem();
        }
        return instance;
    }
    
    public List<RecordsList> loadGenres(String storage){
    	List<RecordsList> genres = new ArrayList<>();
    	try {
			File dir = new File(storage.concat(DOT));
			for(String path : dir.list()){
				if((path.substring(path.lastIndexOf(DOT), path.length()).equals(FILE_EXTENSION)))
					genres.add(new Genre(path.substring(0, path.lastIndexOf(DOT)), (HashSet)deserialize(storage + path, HashSet.class)));
			}
		} catch (ClassNotFoundException | IOException e) {
			ds.DisplayError(e);
		} 
		return genres;
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
			Record newTrack = new Track(args[0], args[1], args[2], args[3], args[4]);
			musicLibrary.insertRecord(newTrack);
			serialize(newTrack.getGenre());
	}
	
	public void setTrack(String trackTitle, String ... args){
		ds.DisplayMessage(STATUS_SETTING);
		Record track = musicLibrary.getRecord(trackTitle);
		try {
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
		} catch (IntrospectionException 
				| IllegalAccessException 
				| IllegalArgumentException 
				| InvocationTargetException e) {
			ds.DisplayError(e);
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

    public void insertRecordsList(String newGenreName) {
		try{
			musicLibrary.insertRecordsList(newGenreName);
			serialize(newGenreName);
		}catch (IllegalArgumentException e) {
			ds.DisplayError(e);
		}
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
			musicLibrary.insertRecordsList(genreNameTo);
		}try{
    		for(Record record:musicLibrary.getRecordsList(genreNameFrom).getRecords()){
        		record.setGenre(genreNameTo);
        		genreTo.insertRecord(record);
        	}
    		musicLibrary.removeRecordsList(genreNameFrom);
    		File file = new File(STORAGE+genreNameFrom+FILE_EXTENSION);
			if(file.exists()) {
				file.delete();
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
		}
	}
	
    private <classType> Object deserialize(String fileName, Class classType) throws IOException, ClassNotFoundException{
		Object obj = null;
		try (ObjectInputStream objectInStream = new ObjectInputStream(new FileInputStream(new File(fileName)));) {
			obj = (classType) objectInStream.readObject();
		}
		return obj;
	}

    public static void main(String[] args){
    	getInstance();
    	System.setProperty(FILE_ENCODING, FILE_ENCODING_VALUE);
    	System.setProperty(CONSOLE_ENCODING, CONSOLE_ENCODING_VALUE);
    	try {
    		System.setOut(new PrintStream(System.out, true, CONSOLE_ENCODING_VALUE));
    	} catch (java.io.UnsupportedEncodingException ex) {
    		ds.DisplayMessage(WARNING_ENCODING);
    	}
    	ds.DisplayMessage(WELCOME_MESSAGE);
    	CommandProcessor cp = new CommandProcessor(ds, CONSOLE_ENCODING_VALUE);
    	cp.execute();
    }

    @Override
    public void doEvent(Object arg) {
       if (arg.getClass().equals(String.class)) ds.DisplayMessage((String)arg);
       if (arg instanceof Exception) ds.DisplayError((Exception) arg);
    }
}
