package output;

import interfaces.Record;

import java.util.Collection;
import java.util.Iterator;

/**
 * Created by Morthanion on 31.10.2014.
 */
public class DisplaySystem {
	private static final String DELIMITER = " | ";
	private static final String ERROR = "Error: ";
	private static final String LIST_FORMATTER = "%-30s %-9s %-30s %-20s %-20s%n";
	private static final String HELP_FORMATTER = "%-50s %-90s%n";
    private DisplaySystem(){}
    
	private static class SingletonHolder {
		private static final DisplaySystem INSTANCE = new DisplaySystem();
	}
	
	public static DisplaySystem getInstance() {
		return SingletonHolder.INSTANCE;
	}

    public void DisplayMessage(String message)
    {
        System.out.println(message);
    }
    public void DisplaySymbols(String symbols)
    {
        System.out.print(symbols);
    }
    public void DisplayError(Exception e)
    {
        System.out.println(ERROR + e.getMessage());
       // e.printStackTrace();
    }
        public void DisplayList(Collection<Record> list) {
              System.out.printf(LIST_FORMATTER,
            		  Field.Title.name(),
                DELIMITER+Field.Length.name(),
                DELIMITER+Field.Album.name(),
                DELIMITER+Field.Genre.name(),
                DELIMITER+Field.Singer.name());
        Iterator<Record> iterator = list.iterator();
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
