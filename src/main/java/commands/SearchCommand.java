package commands;

import interfaces.Command;
import management.ManagementSystem;
import output.HelpContainer;

/**
 * Created by Morthanion on 06.11.2014.
 */
public class SearchCommand implements Command{

	private static final String COMMAND_DESCRIPTION = "Defines operations with genre list";
	private static final String COMMAND_NAME = "SEARCH";
	private static final String WARNING_NO_COMMAND_PARAMETER = "You must specify the parameter. Type \"help search\" to view available";
	private static final String WARNING_SUBCOMMAND = "Enter <search mask> to process";
	private static final String SUBCOMMAND_TITLE_FORMAT = "-t <search mask>";
	private static final String SUBCOMMAND_TITLE_FORMAT_DESCRIPTION = "search by title";
	private static final String SUBCOMMAND_ALBUM_FORMAT = "-a <search mask>";
	private static final String SUBCOMMAND_ALBUM_FORMAT_DESCRIPTION = "search by album name";
	private static final String SUBCOMMAND_GENRE_FORMAT = "-g <search mask>";
	private static final String SUBCOMMAND_GENRE_FORMAT_DESCRIPTION = "search by genre";
	private static final String SUBCOMMAND_SINGER_FORMAT = "-s <search mask>";
	private static final String SUBCOMMAND_SINGER_FORMAT_DESCRIPTION = "search by singer";
	private static final String SUBCOMMAND_LENGTH_FORMAT = "-l <search mask>";
	private static final String SUBCOMMAND_LENGTH_FORMAT_DESCRIPTION = "search by length";
	private static final String MASK_DESCRIPTION = "search is case-insensitive \r\n" +
			"search mask example \" *m?3 \"\r\n"+
			"where \"*\" - any or none symbols\r\n"+
			"      \"?\" - any or none single symbol\r\n";
    private static  ManagementSystem ms;
    public SearchCommand()
    {
        SearchCommand.ms = ManagementSystem.getInstance();
    }
    
    @Override
    public boolean execute(String... args) {
        if (args == null)
        	throw new IndexOutOfBoundsException(WARNING_NO_COMMAND_PARAMETER);
        else try{
        	SubCommand subCommand = SubCommand.getName(args[0]);
			if(args.length < 2)
				throw new IndexOutOfBoundsException(WARNING_SUBCOMMAND);
			else 
				subCommand.process(args);		
		} catch (IllegalArgumentException e){
			throw new IllegalArgumentException(e.getMessage());
		}
        return true;
    }

    @Override
    public void printHelp() {
    	for(SubCommand sc: SubCommand.values())
    		ms.doEvent(new HelpContainer(sc.getFormat(), sc.getDescription()));
    	ms.doEvent(MASK_DESCRIPTION);
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return COMMAND_DESCRIPTION;
    }
    
    private enum SubCommand{
		TITLE(SUBCOMMAND_TITLE_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_TITLE_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_TITLE_FORMAT;
			}

			@Override
			public void process(String... args) {
				ms.searchItems(TITLE.getKey(), args[1]);
			}
		}, 
		ALBUM(SUBCOMMAND_ALBUM_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_ALBUM_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_ALBUM_FORMAT;
			}

			@Override
			public void process(String... args) {
				ms.searchItems(ALBUM.getKey(), args[1]);
			}
		}, 
		GENRE(SUBCOMMAND_GENRE_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_GENRE_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_GENRE_FORMAT;
			}

			@Override
			public void process(String... args) {
				ms.searchItems(GENRE.getKey(), args[1]);
			}
		}, 
		SINGER(SUBCOMMAND_SINGER_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_SINGER_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return  SUBCOMMAND_SINGER_FORMAT;
			}

			@Override
			public void process(String... args) {
				ms.searchItems(SINGER.getKey(), args[1]);
			}
		}, 
		LENGTH(SUBCOMMAND_LENGTH_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_LENGTH_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_LENGTH_FORMAT;
			}

			@Override
			public void process(String... args) {
				ms.searchItems(LENGTH.getKey(), args[1]);
			}
		};
		
		private final String key;
	    SubCommand(String key) {
	        this.key = key;
	    }
		public abstract String getFormat();
		public abstract String getDescription();
		public abstract void process(String...args);
		
		public static SubCommand getName(String key) {
	        for (SubCommand sCom: SubCommand.values()) {
	            if (sCom.getKey().equals(key)) {
	                return sCom;
	            }
	        }
	        throw new IllegalArgumentException(COMMAND_NOT_FOUND);
	    }
		public  String getKey(){
			return this.key;	
		}
	}
}
