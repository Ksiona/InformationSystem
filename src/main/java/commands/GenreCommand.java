package commands;

import interfaces.Command;
import management.ManagementSystem;
import output.HelpContainer;

public class GenreCommand implements Command {

	private static final String COMMAND_DESCRIPTION = "Defines operations with genres";
	private static final String COMMAND_NAME = "GENRE";
	private static final String WARNING_NO_COMMAND_PARAMETER = "You must specify the parameter. Type \"help genre /\" to view available";
	private static final String WARNING_SUBCOMMAND_SET = "Enter the old name of the genre and new to continue";
	private static final String WARNING_SUBCOMMAND_COMBINE = "The option \"combine genres\" need parameters <genre1 name> <genre2 name> <new genre name>";
	private static final String WARNING_SUBCOMMAND_INSERT = "Enter genre name to add";
	private static final String WARNING_SUBCOMMAND_REMOVE = "Enter genre name to remove";
	private static final String WARNING_SUBCOMMAND_PRINT_TRACKS = "Enter genre name for print it tracks";
	private static final String SUBCOMMAND_REMOVE_FORMAT = "-r <genre name> /";
	private static final String SUBCOMMAND_REMOVE_FORMAT_DESCRIPTION = "remove genre from list";
	private static final String SUBCOMMAND_INSERT_FORMAT = "-a <new genre name> /";
	private static final String SUBCOMMAND_INSERT_FORMAT_DESCRIPTION = "add genre into list";
	private static final String SUBCOMMAND_COMBINE_FORMAT = "-c <genre1 name> <genre2 name> <new genre name> /";
	private static final String SUBCOMMAND_COMBINE_FORMAT_DESCRIPTION = "combines tracks of genre1 and genre2 into new genre name pack";
	private static final String SUBCOMMAND_PRINT_FORMAT = "-p /";
	private static final String SUBCOMMAND_PRINT_FORMAT_DESCRIPTION = "print genre list";
	private static final String SUBCOMMAND_PRINT_TRACKS_FORMAT = "-pt <genre name> /";
	private static final String SUBCOMMAND_PRINT_TRACKS_FORMAT_DESCRIPTION = "print tracks list of this genre";
	private static final String SUBCOMMAND_SET_FORMAT = "-s <old genre name> <new genre name> /";
	private static final String SUBCOMMAND_SET_FORMAT_DESCRIPTION = "set new genre name";
	
	private static ManagementSystem ms;
	
    public GenreCommand() {
		GenreCommand.ms = ManagementSystem.getInstance();
	}
	
	@Override
	public boolean execute(String... args) {
		 if (args == null) {
			 throw new IndexOutOfBoundsException(WARNING_NO_COMMAND_PARAMETER);
		 } else try{
			 SubCommand subCommand = SubCommand.getName(args[0]);
			 if((args.length -1) < subCommand.getMethodParametersQuantity())
				 throw new IndexOutOfBoundsException(subCommand.getWarning());
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
				return 1;
			}

			@Override
			public void process(String... args) {
				ms.insertRecordsList(args[1]);
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
				return 1;
			}

			@Override
			public void process(String... args) {
				ms.removeRecordsList(args[1]);
			}
		}, 
		COMBINE(SUBCOMMAND_COMBINE_FORMAT.substring(0, 2)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_COMBINE_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_COMBINE_FORMAT;
			}

			@Override
			public String getWarning() {
				return WARNING_SUBCOMMAND_COMBINE;
			}

			@Override
			public int getMethodParametersQuantity() {
				return 3;
			}

			@Override
			public void process(String... args) {
				ms.combineRecordsLists(args[1], args[2], args[3]);
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
				return 2;
			}

			@Override
			public void process(String... args) {
				ms.setRecordsListName(args[1], args[2]); 
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
				ms.getRecordsListsName();
			}
			
		},
		PRINT_TRACKS(SUBCOMMAND_PRINT_TRACKS_FORMAT.substring(0, 3)){
			@Override
			public String getDescription(){
				return SUBCOMMAND_PRINT_TRACKS_FORMAT_DESCRIPTION;
			}

			@Override
			public String getFormat() {
				return SUBCOMMAND_PRINT_TRACKS_FORMAT;
			}

			@Override
			public String getWarning() {
				return WARNING_SUBCOMMAND_PRINT_TRACKS;
			}

			@Override
			public int getMethodParametersQuantity() {
				return 1;
			}

			@Override
			public void process(String... args) {
				ms.getTracksTitles(args[1]);
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
