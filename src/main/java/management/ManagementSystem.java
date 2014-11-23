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

import commands.SelectionProcessor;
import output.DisplaySystem;
import output.ListContainer;

/**
 * Управляет изменениями в модели и отправляет информацию на выход
 */
public class ManagementSystem implements Listener {
	
	private static final ManagementSystem INSTANCE = new ManagementSystem();
	private static final String STORAGE = "Storage/";
	private static final String UNSORTED_RECORDSLIST_NAME = "Unsorted";
	private static final String FILE_EXTENSION = ".bin";
	private static final String DOT = ".";
	private static final String STATUS_REMOVING ="Removing track...";
	private static final String STATUS_SETTING = "Set changes...";
	private static final String STATUS_INSERTING = "Inserting track...";
	private static final String STATUS_WRITING_TO_FILE_SUCCESS = "Successfully updated storage: ";
	private static final String STATUS_REMOVE_FILE_SUCCESS = "Successfully remove storage: ";
	private static final String STATUS_USER_ABORTED ="Operation aborted by user";
    private static final String STATUS_NO_CHANGES = "None of the tracks has not been changed, check the entered parameters";
	private static final String FILE_HAS_BEEN_DELETED = " file has been deleted";
	private static final String WARNING_GENRE_ALREADY_EXIST = " genre already exist";
	private static final String WARNING_WRONG_PARAMETER = "Operation aborted, check the passed parameters, please";
	private static final String WARNING_CHECK_STORAGE = "Operation aborted, not found the main store, check the folder \"STORAGE\" in the program folder";
	private static final String REMOVE_QUESTION = "Above are several tracks with the same name, are you want to delete all the tracks? \r\n"
			+ "\"Y\" - to delete all tracks, \r\n"
			+ "\"N\" - to abort the operation, \r\n"
			+ "\"S\" - to select a specific track";
	private enum Answers{Y,N,S;}
	
	private static Collection<Listener> listeners;
    private static final Logger log = Logger.getLogger(ManagementSystem.class);

	private Library musicLibrary;
    private List<String> genreFilesDuplicates = new ArrayList<>();
	private Thread selectionThread;
	private String selection;
	private SelectionProcessor sp;

	private ManagementSystem(){
        ManagementSystem.listeners = new HashSet<>();
        ManagementSystem.listeners.add(DisplaySystem.getInstance());
        MusicLibrary library = new MusicLibrary(loadGenres(STORAGE));
        library.AddListener(this);
        this.musicLibrary = library;
    }

    /**
     * Возвращет ссылку на единственный экземпляр класса
     * @return ссылку на единственный экземпляр класса
     */
	public static ManagementSystem getInstance() {
		return INSTANCE;
	}

    /**
     * Загружает все файлы из хранилища
     * Создает список из жанров, содержащихся более чем в 1 файле
     * @param storage адрес хранилища
     * @return Список всех списков жанра
     */
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
		} catch (NullPointerException e) {
            notifyListeners(WARNING_CHECK_STORAGE);
			log.error(e.getMessage(), e);
		} catch (ClassNotFoundException | IOException e) {
			notifyListeners(e);
			log.error(e.getMessage(), e);
		} 
		return genres;
    }

    /**
     * Записывает все несохраненные изменения в файловую систему
     * Сравнивает список жанров со списком имен файлов в папке с библиотекой
     * файлы с расширением bin и названиями не найдеными в списке жанров, удаляются
     */
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
		    	if(path.endsWith(FILE_EXTENSION) && new File(STORAGE + path).isFile())
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

    /**
     * Отправляет на вывод список всех записей в бибилотеке
     */
	public void printAllTracksInfo(){
		notifyListeners(new ListContainer(musicLibrary.getAllRecords()));
    }

    /**
     * Отправляет на вывод список всех записей конкретного жанра,
     * если такого жанра нет - отправляет на вывод
     * сообщение об ошибке
     * @param genreName название нужного жанра
     */
	public void getTracksTitles(String genreName){
    	try{
    		notifyListeners(new ListContainer(musicLibrary.getRecordsList(genreName).getRecords()));
    	} catch (IllegalArgumentException e){
    		notifyListeners(e);
    	}
    }

    /**
     * Перемещает трек с определенным названием в новый жанр,
     * создавая новый список, если это необходимо.
     * В случае если трека с данным названием не существует,
     * отправляет на вывод сообщение об ошибке
     * @param trackTitle название трека для переноски
     * @param genreName название жанра, куда следует переместить трек
     */
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
    		notifyListeners(e);
    	}
    }

    /**
     * Отправляет на вывод информацию о треках с определенным названием, если таковых нет - отправляет сообщение об этом
     * @param trackTitle название трека для вывода
     */
    public void printTrackInfo(String trackTitle){
    	List<Record> rList = new ArrayList<>();
    	for (Record track : musicLibrary.getAllRecords())
			if (track.getTrackTitle().equalsIgnoreCase(trackTitle)){
				rList.add(track);
			}
    	if (rList.size() != 0)notifyListeners(new ListContainer(rList));
        else notifyListeners("Tracks named "+trackTitle+" were not found");
    }

    /**
     * Добавляет один или несколько новых треков в библиотеку,
     * в ходе добавления заполняется список жанров, в которые были внесены изменения.
     * Запись в файловую систему осуществляется после добавления всех треков, по созданному списку жанров
     * @param args массив параметров трека в последовательности <br>
     *             { жанр, название, исполнитель, альбом, длинна трека }
     * @exception  ArrayIndexOutOfBoundsException - передано количество аргументов не кратное количеству заполняемых полей трека
     * треки добавленные до исключительной ситуации сохраняются.
     */
	public void insertTrack(String ... args) {
		Collection<String> genreList = null;
		try{
			notifyListeners(STATUS_INSERTING);
			genreList = new HashSet<>();
			for(int i=0;i<args.length; i=i+5){
				Record newTrack = new Track (args[i], args[i+1], args[i+2], args[i+3], args[i+4]);
				if (((i)%5)==0 && newTrack !=null){
					musicLibrary.insertRecord(newTrack);
					genreList.add(newTrack.getGenre());
				}
			}
		}catch(ArrayIndexOutOfBoundsException e){
			throw new IllegalArgumentException(WARNING_WRONG_PARAMETER);
		}finally{
			if(!genreList.isEmpty())
				for (String genreName:genreList)
					serialize(genreName);
		}
	}

    /**
     * Изменяет трек под соответствующим названием
     * @param trackTitle название трека для изменения
     * @param args массив параметров трека и новых значений <br>
     * может быть передано любое количество пар {параметр - значение}
     * @exception IllegalArgumentException - переданы не парные аргументы 
     * @exception ArrayIndexOutOfBoundsException - передано "значение" равное "параметру"
     * @exception Ошибки интроспекции или рефлексивных вызовов методов
     */
	public void setTrack(String trackTitle, String ... args){
		if(args.length%2 != 1)
			throw new IllegalArgumentException(WARNING_WRONG_PARAMETER);
		notifyListeners(STATUS_SETTING);
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
			notifyListeners(e);
			log.error(e);
		}
	}

    /**
     * Удаляет трек под данным названием из данного списка жанра
     * Если списка или трека не существует, отсылает на вывод соответствующую ошибку
     * @param trackTitle название трека для удаления
     * @param genreName название жанра, к которому приндалежит этот трек
     */
    public void removeRecord(String trackTitle, String genreName){
    	try{
    		notifyListeners(STATUS_REMOVING);
    		Record currentTrack = musicLibrary.getRecordsList(genreName).getRecord(trackTitle);
    		musicLibrary.removeRecord(genreName, currentTrack);
			serialize(genreName);
    	} catch (IllegalArgumentException e){
    		notifyListeners(e);
    	}
    }

    /**
     * Удаляет треки с данным названием, в случае, если их несколько -
     * отправляет на вывод диалог с предупреждением и поступает соответственно ответу
     * пока ответ не получен основной поток исполнения приостановлен
     * @param trackTitle название трека для удаления
     */
	public void removeRecord(String trackTitle) {
		List<Record> rList = new ArrayList<>();
		for (Record track : musicLibrary.getAllRecords())
			if (track.getTrackTitle().equalsIgnoreCase(trackTitle)){
				rList.add(track);
			}
		if (rList.size()<=1)
			removeRecord(trackTitle, rList.get(0).getGenre());
		else{
			notifyListeners(new ListContainer(rList));
			removeCaseSeveralRecords(trackTitle, rList);
			try {
				selectionThread.join();
			} catch (InterruptedException e) {
				log.warn(e.getMessage(), e);
			}
		}
	}
	
    /**
     * Обработка в отдельном потоке решения пользователя
     * @param trackTitle название трека для удаления
     * @param rList - список треков с таким названием
     */
	private void removeCaseSeveralRecords(final String trackTitle, final List<Record> rList){
		selectionThread = new Thread(new Runnable() {	
			@Override
			public void run() {
				boolean isFinded =false;
				//I can't move the initialization of this resource in the constructor while MS intermediary for console output, 
				//because MS - resource for SelectionProcessor
				sp = new SelectionProcessor();
				selection = sp.askUserChoice(REMOVE_QUESTION);
				switch(Answers.valueOf(selection)){
				case S:
					String[] args = sp.removeCaseParameters();
					for (Record track : rList)
						if (track.getSinger().equalsIgnoreCase(args[0])&& track.getAlbum().equalsIgnoreCase(args[1])){
							removeRecord(trackTitle, track.getGenre());
							isFinded =true;
						}
					if(!isFinded)
						notifyListeners(STATUS_NO_CHANGES);
					break;
				case Y:
					for (Record track : rList)
						removeRecord(trackTitle, track.getGenre());
					break;
				case N:
					notifyListeners(STATUS_USER_ABORTED);
					break;
				}
			}
		});
		selectionThread.setDaemon(true);
		selectionThread.start();
	}

    /**
     * Добавляет в библиотеку список нового жанра, если таковой
     * жанр уже существует - отправляет на вывод соответсвенное сообщение
     * @param genreName название нового жанра
     */
	public void insertRecordsList(String genreName) {
		Collection<Record> newGenreTracks = new HashSet<>();
		if(!musicLibrary.checkExist(genreName)){
			musicLibrary.insertRecordsList(genreName, newGenreTracks);
			serialize(genreName);
		}
		else
			notifyListeners(WARNING_GENRE_ALREADY_EXIST);
	}

    /**
     * Удаляет жанр и перемещает все его треки в Unsorted
     * @param genreName название жанра на удаление
     */
    public void removeRecordsList(String genreName){
    	try{
    		combineRecordsLists(UNSORTED_RECORDSLIST_NAME, genreName, UNSORTED_RECORDSLIST_NAME);
			notifyListeners(STATUS_REMOVE_FILE_SUCCESS + genreName);
		}catch (IllegalArgumentException e) {
			notifyListeners(e);
		}
    }

    /**
     * Перемещает все треки из одного списка жанра в другой, создавая последний при необходимости.
     * В случае, если отсутствует исходный список жанра, отправляет на вывод сообщение об этом
     * @param genreNameFrom название исходного жанра
     * @param genreNameTo название жанра, куда нужно переместить треки
     */
    public void moveAllRecordsAnotherSet(String genreNameFrom, String genreNameTo){
    	RecordsList genreTo = null;
    	try{
    		genreTo  = musicLibrary.getRecordsList(genreNameTo);
    	}catch (IllegalArgumentException e){
			insertRecordsList(genreNameTo);
			genreTo  = musicLibrary.getRecordsList(genreNameTo);
		}try{
    		for(Record record:musicLibrary.getRecordsList(genreNameFrom).getRecords()){
        		record.setGenre(genreNameTo);
        		genreTo.insertRecord(record);
        	}
    		musicLibrary.removeRecordsList(genreNameFrom);
    		File file = new File(STORAGE+genreNameFrom+FILE_EXTENSION);
    		file.deleteOnExit();;
    		log.info(STORAGE + genreNameFrom + FILE_HAS_BEEN_DELETED);
    	}catch (NullPointerException | IllegalArgumentException e){
    		notifyListeners(e);
		}
    }

    /**
     * Объединяет два списка по жанру в один новый
     * @param genreName1 название первого жанра
     * @param genreName2 название второго жанра
     * @param newGenreName название результирующего жанра
     * @see moveAllRecordsAnotherSet()
     */
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

    /**
     * Отправляет на вывод список всех жанров библиотеки
     */
    public void getRecordsListsName(){
    	for (RecordsList genre: musicLibrary.getRecordsLists())
            notifyListeners(genre.getRecordsListName());
    }

    /**
     * Изменяет название жанра
     * @param oldGenreName старое название
     * @param newGenreName новое название
     * @see moveAllRecordsAnotherSet()
     */
	public void setRecordsListName(String oldGenreName, String newGenreName) {
		notifyListeners(STATUS_SETTING);
		moveAllRecordsAnotherSet(oldGenreName, newGenreName);
		serialize(newGenreName);
	}

    /**
     * Отправляет на вывод список всех треков, подходящих под маску поиска.
     * @param keyField Название ключевого поля (Albium/Title/Genre/Singer/Length).
     * @param mask Маска поиска - последовательность символов, которая должна встречаться в лючевом поле где<br>
     *  "*" - ни одного или более любых символов. <br>
     *  "?" - один любой символ.
     */
    public void searchItems(String keyField, String mask)
    {
        Collection<Record> fits = new ArrayList<Record>();
        Iterator<Record> trackIterator = musicLibrary.getAllRecords().iterator();
        while (trackIterator.hasNext())
        {
            Record checked = trackIterator.next();
            if (checked.fitsMask(keyField, mask)) fits.add(checked);
        }
        notifyListeners(new ListContainer(fits));
    }
    
    /**
     * Запись объекта в файловую систему с использованием механизма сериализации, 
     * создается файл с именем объекта и расширением .bin
     * @param objName - название объекта.
     */
    private void serialize(String objName){
		try (ObjectOutputStream objectOutStream = new ObjectOutputStream(new FileOutputStream(new File(STORAGE+objName+FILE_EXTENSION)))){		
	    	Object obj = musicLibrary.getRecordsList(objName).getRecords();
			objectOutStream.writeObject(obj);
			notifyListeners(STATUS_WRITING_TO_FILE_SUCCESS + objName);
		}catch (IOException e) {
			notifyListeners(e);
			log.warn(e.getMessage(), e);
		}
	}
	
    /**
     * Восстановление объекта из файла с использованием механизма сериализации, 
     * @param fileName - имя файла.
     * @param classType - класс, объект которого мы рассчитываем восстановить 
     */
    private <classType> Object deserialize(String fileName, Class<?> classType) throws IOException, ClassNotFoundException{
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
    	notifyListeners(arg);
    }

    /**
     * Оповещает о событии всех слушателей, переправляя им сопутствующую информацию
     * @param arg информация для передачи
     */
    public void notifyListeners(Object arg) {
       for(Listener listener: ManagementSystem.listeners) listener.doEvent(arg);
    }
}
