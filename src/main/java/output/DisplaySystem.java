package output;

import interfaces.Listener;
import interfaces.Record;

import java.io.PrintStream;
import java.util.Iterator;

/**
 * Created by Morthanion on 31.10.2014.
 */
public class DisplaySystem implements Listener {
	private static final DisplaySystem INSTANCE = new DisplaySystem();
	private static final String DELIMITER = " | ";
	private static final String ERROR = "Error: ";
	private static final String LIST_FORMATTER = "%-30s %-10s %-35s %-20s %-25s%n";
	private static final String HELP_FORMATTER = "%-50s %-70s%n";
    private static final PrintStream displayStream = new PrintStream(System.out);
    private DisplaySystem(){}

    /**
     * Выводит объект на экран. Поддерживаемые объекты:<ul>
     *     <li>String - выводит сообщение
     *     <li>Exception - выводит сообщение об ошибке, используя Exception.getMessage() как его основу
     *     <li>HelpContainer - выводит справку о команде
     *     <li>ListContainer - выводит список записей
     *     </ul>
     * @param arg объект для вывода
     */
    @Override
    public void doEvent(Object arg){
        if (arg instanceof ListContainer){
            DisplayList(((ListContainer) arg));
        }
        else if (arg instanceof Exception){
            DisplayError((Exception)arg);
        }
        else if (arg instanceof String){
            DisplayMessage((String) arg);
        }
        else if (arg instanceof HelpContainer) {
            DisplayHelp(((HelpContainer) arg).getName(), ((HelpContainer) arg).getDescription());
        }
        else if (arg instanceof StringContainer){
        	DisplayMessageNewlineOff((StringContainer) arg);
        }
        else throw new IllegalArgumentException("Output of "+arg.getClass().toString()+" is not implemented");
    }

    /**
     * Возвращет ссылку на единственный экземпляр класса
     * @return сслыку на единственный экземпляр
     */
	public static DisplaySystem getInstance() {
		return INSTANCE;
	}

    private void DisplayMessage(String message)
    {
        displayStream.println(message);
    }
    
    private void DisplayMessageNewlineOff(StringContainer message){
        displayStream.print(message.getLine());
    }
    
    private void DisplayError(Exception e){
        displayStream.println(ERROR + e.getMessage());
       // e.printStackTrace();
    }
    private void DisplayList(ListContainer list) {
              displayStream.printf(LIST_FORMATTER,
            		  Field.Title.name(),
                DELIMITER+Field.Length.name(),
                DELIMITER+Field.Album.name(),
                DELIMITER+Field.Genre.name(),
                DELIMITER+Field.Singer.name());
        Iterator<Record> iterator = list.getCollection().iterator();
        while (iterator.hasNext()) {
            Record out = iterator.next();
            displayStream.printf(LIST_FORMATTER,
                    out.getTrackTitle(),
                    DELIMITER+out.getRecordLength(),
                    DELIMITER+out.getAlbum(),
                    DELIMITER+out.getGenre(),
                    DELIMITER+out.getSinger());
        }
    }
    private void DisplayHelp(String name, String description) {
        displayStream.printf(HELP_FORMATTER, name, description);
	}

		private enum Field{Title,Singer,Album,Length,Genre}
}
