package commands;

import org.apache.log4j.Logger;

import interfaces.Command;
import management.ManagementSystem;
import output.DisplaySystem;

class TrackCommand implements Command {
	
	private static final String COMMAND_DESCRIPTION = "Defines operations with tracks";
	private static final String COMMAND_NAME = "TRACK";
	private static final String WARNING_NO_COMMAND_PARAMETER = "You must specify the parameter. Type \"help track\" to view available";
	private static final String WARNING_SUBCOMMAND_INFO = "Enter track title to process";
	private static final String WARNING_SUBCOMMAND_SET = "Enter track title and parameters with new values to process";
	private static final String WARNING_SUBCOMMAND_INSERT = "Don't skip parameters, if don't no info - type \"-\" \r\n" +
	 												"Example: -a \"genre\" \"title\" \"singer\" \"Album\" Record length";
	private static final String WARNING_SUBCOMMAND_REMOVE = "Enter track title and genre name to remove";
	private static final String WARNING_SUBCOMMAND_SET_GENRE = "Enter track title and genre name to process";
	private static final String SUBCOMMAND_INFO_FORMAT = "-i <track title>";
	private static final String SUBCOMMAND_INFO_FORMAT_DESCRIPTION = "get track info";
	private static final String SUBCOMMAND_SET_FORMAT = "-s <track title> <parameter> <new value>";
	private static final String SUBCOMMAND_SET_FORMAT_DESCRIPTION = "set track info, you can change several parameters at once,"
																	+ "parameters names: <title> <singer> <album> <length>"
																	+ "use the key -g to set track genre, please";
	private static final String SUBCOMMAND_INSERT_FORMAT = "-a <track parameters>";
	private static final String SUBCOMMAND_INSERT_FORMAT_DESCRIPTION = "add track into library, enter parameters in sequence "+
																		": <genre> <track title> <singer> <album> <record length>";
	private static final String SUBCOMMAND_REMOVE_FORMAT = "-r <track title> <genre name>";
	private static final String SUBCOMMAND_REMOVE_FORMAT_DESCRIPTION = "remove track with title from genre";
	private static final String SUBCOMMAND_SETGENRE_FORMAT = "-g <track title> <genre name>";
	private static final String SUBCOMMAND_SET_GENRE_FORMAT_DESCRIPTION = "set another genre for track";
	private static final String SUBCOMMAND_PRINT_FORMAT = "-p";
	private static final String SUBCOMMAND_PRINT_FORMAT_DESCRIPTION = "print titles of all available tracks";
	private static final String ARGUMENT_LENGTH_REPLACEMENT = "recordLength";
	private static final String ARGUMENT_TITLE_REPLACEMENT = "trackTitle";
	private static final Logger log = Logger.getLogger(TrackCommand.class);
	
	private static ManagementSystem ms;
	private DisplaySystem ds;
	
    public TrackCommand() {
		this.ds = DisplaySystem.getInstance();
		this.ms = ManagementSystem.getInstance();
	}
    
	@Override
    public boolean execute(String... args) {
		if (args == null) 
			ds.DisplayMessage(WARNING_NO_COMMAND_PARAMETER);
		else try{
			SubCommand subCommand = SubCommand.getName(args[0]);
			if((args.length -1) < subCommand.getMethodParametersQuantity())
				ds.DisplayMessage(subCommand.getWarning());	
			else 
				subCommand.process(args);		
		} catch (IllegalArgumentException e){
			ds.DisplayError(e);
			log.warn(e.getMessage(), e);
		}
        return true;
    }

    @Override
    public void printHelp() {
    	for(SubCommand sc: SubCommand.values())
			ds.DisplayHelp(sc.getFormat(), sc.getDescription());
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
		INFO(SUBCOMMAND_INFO_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_INFO_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_INFO_FORMAT;
			}

			@Override
			public String getWarning() {
				return WARNING_SUBCOMMAND_INFO;
			}

			@Override
			public int getMethodParametersQuantity() {
				return 1;
			}

			@Override
			public void process(String... args) {
				ms.printTrackInfo(args[1]); 
			}
		}, 
		SET(SUBCOMMAND_SET_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_SET_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return  SUBCOMMAND_SET_FORMAT;
			}

			@Override
			public String getWarning() {
				return WARNING_SUBCOMMAND_SET;
			}

			@Override
			public int getMethodParametersQuantity() {
				return 1;
			}

			@Override
			public void process(String... args) {
				for(int i=0;i<args.length;i++){
					if (args[i].equalsIgnoreCase(Field.LENGTH.name()))
						args[i] = Field.LENGTH.replacement;
					if (args[i].equalsIgnoreCase(Field.TITLE.name()))
						args[i] = Field.TITLE.replacement;
				}
				ms.setTrack(args[1], args);
			}
		}, 
		INSERT(SUBCOMMAND_INSERT_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_INSERT_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_INSERT_FORMAT;
			}

			@Override
			public String getWarning() {
				return WARNING_SUBCOMMAND_INSERT;
			}

			@Override
			public int getMethodParametersQuantity() {
				return 5;
			}

			@Override
			public void process(String... args) {
				ms.insertTrack(args);
			}
		},
		GENRE(SUBCOMMAND_SETGENRE_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_SET_GENRE_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_SETGENRE_FORMAT;
			}

			@Override
			public String getWarning() {
				return WARNING_SUBCOMMAND_SET_GENRE;
			}

			@Override
			public int getMethodParametersQuantity() {
				return 2;
			}

			@Override
			public void process(String... args) {
				ms.moveRecordAnotherSet(args[1], args[2]);
			}
		}, 
		PRINT(SUBCOMMAND_PRINT_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_PRINT_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_PRINT_FORMAT;
			}

			@Override
			public String getWarning() {
				return COMMAND_NOT_FOUND;
			}

			@Override
			public int getMethodParametersQuantity() {
				return 0;
			}

			@Override
			public void process(String... args) {
				ms.printAllTracksTitle();	
			}
			
		},
		REMOVE(SUBCOMMAND_REMOVE_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_REMOVE_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_REMOVE_FORMAT;
			}

			@Override
			public String getWarning() {
				return WARNING_SUBCOMMAND_REMOVE;
			}

			@Override
			public int getMethodParametersQuantity() {
				return 2;
			}

			@Override
			public void process(String... args) {
				ms.removeRecord(args[1], args[2]);
			}
		};
		
		private final String key;
	    SubCommand(String key) {
	        this.key = key;
	    }
		public abstract String getFormat();
		public abstract String getDescription();
		public abstract String getWarning();
		public abstract int getMethodParametersQuantity();
		public abstract void process(String...args);
		
		public static SubCommand getName(String key) {
	        for (SubCommand sCom: SubCommand.values()) 
	            if (sCom.getKey().equals(key)) 
	                return sCom;
	        throw new IllegalArgumentException(COMMAND_NOT_FOUND);
	    }
		
		public  String getKey(){
			return this.key;	
		}
	}
	
	private enum Field{
		LENGTH(ARGUMENT_LENGTH_REPLACEMENT),
		TITLE(ARGUMENT_TITLE_REPLACEMENT);
		private final String replacement;
	    Field(String replacement) {
	        this.replacement = replacement;
	    }
	}
}