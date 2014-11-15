package output;

import interfaces.Listener;
import interfaces.Record;

import java.util.Iterator;

/**
 * Created by Morthanion on 31.10.2014.
 */
public class DisplaySystem implements Listener {
	private static final String DELIMITER = " | ";
	private static final String ERROR = "Error: ";
	private static final String LIST_FORMATTER = "%-30s %-10s %-35s %-20s %-25s%n";
	private static final String HELP_FORMATTER = "%-50s %-70s%n";
    private DisplaySystem(){}

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
        else throw new IllegalArgumentException("Output of "+arg.getClass().toString()+" is not implemented");
    }

    private static class SingletonHolder {
		private static final DisplaySystem INSTANCE = new DisplaySystem();
	}
	
	public static DisplaySystem getInstance() {
		return SingletonHolder.INSTANCE;
	}

    private void DisplayMessage(String message)
    {
        System.out.println(message);
    }
    private void DisplayError(Exception e)
    {
        System.out.println(ERROR + e.getMessage());
       // e.printStackTrace();
    }
    private void DisplayList(ListContainer list) {
              System.out.printf(LIST_FORMATTER,
            		  Field.Title.name(),
                DELIMITER+Field.Length.name(),
                DELIMITER+Field.Album.name(),
                DELIMITER+Field.Genre.name(),
                DELIMITER+Field.Singer.name());
        Iterator<Record> iterator = list.getCollection().iterator();
        while (iterator.hasNext()) {
            Record out = iterator.next();
            System.out.printf(LIST_FORMATTER,
                    out.getTrackTitle(),
                    DELIMITER+out.getRecordLength(),
                    DELIMITER+out.getAlbum(),
                    DELIMITER+out.getGenre(),
                    DELIMITER+out.getSinger());
        }
    }
		public void DisplayHelp(String name, String description) {
			System.out.printf(HELP_FORMATTER, name, description);
		}
		
		private enum Field{Title,Singer,Album,Length,Genre}
}
