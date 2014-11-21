package commands;

import java.util.ArrayList;
import java.util.List;

import interfaces.Command;
import management.ManagementSystem;
import output.HelpContainer;

class TrackCommand implements Command {
	
	private static final String COMMAND_DESCRIPTION = "Defines operations with tracks";
	private static final String COMMAND_NAME = "TRACK";
	private static final String WARNING_SUBCOMMAND_INFO = "Enter track title to process";
	private static final String WARNING_SUBCOMMAND_SET = "Enter track title and parameters with new values to process";
	private static final String WARNING_SUBCOMMAND_INSERT = "Don't skip parameters, if don't no info - type \"-\" \r\n" +
	 												"Example: -a \"genre\" \"title\" \"singer\" \"album\" record length";
	private static final String WARNING_SUBCOMMAND_REMOVE = "Enter track title to remove";
	private static final String WARNING_SUBCOMMAND_SET_GENRE = "Enter track title and genre name to process";
	private static final String SUBCOMMAND_INFO_FORMAT = "-i <track title>";
	private static final String SUBCOMMAND_INFO_FORMAT_DESCRIPTION = "get track info";
	private static final String SUBCOMMAND_SET_FORMAT = "-s <track title> <parameter> <new value>";
	private static final String SUBCOMMAND_SET_FORMAT_DESCRIPTION = "set track info, you can change several parameters at once, \r\n\n"
																	+ "parameters names: <title> <singer> <album> <length>, don't enter value is identical to the parameter, it will cause errors, and "
																	+ "use the key -g to set track genre, please \r\n";
	private static final String SUBCOMMAND_INSERT_FORMAT = "-a <track parameters> /";
	private static final String SUBCOMMAND_INSERT_FORMAT_DESCRIPTION = "add track into library, \r\n\n"
																	+ "enter parameters in sequence: <genre> <track title> <singer> <album> <record length> "
																	+ "you can insert several tracks at once, this command must be terminated by a character \"/\" \r\n";
	private static final String SUBCOMMAND_REMOVE_FORMAT = "-r <track title>";
	private static final String SUBCOMMAND_REMOVE_FORMAT_DESCRIPTION = "remove track with title from library";
	private static final String SUBCOMMAND_SETGENRE_FORMAT = "-g <track title> <genre name>";
	private static final String SUBCOMMAND_SET_GENRE_FORMAT_DESCRIPTION = "set another genre for track";
	private static final String SUBCOMMAND_PRINT_FORMAT = "-p ";
	private static final String SUBCOMMAND_PRINT_FORMAT_DESCRIPTION = "print titles of all available tracks";
	private static final String ARGUMENT_LENGTH_REPLACEMENT = "recordLength";
	private static final String ARGUMENT_TITLE_REPLACEMENT = "trackTitle";
	
	private static ManagementSystem ms;
	private List<SubCommand> subCommands;
	private SubCommand subCommand;
	
    public TrackCommand() {
		TrackCommand.ms = ManagementSystem.getInstance();
		subCommands = new ArrayList<>();
		subCommands.add(new SubCommand(SUBCOMMAND_REMOVE_FORMAT, SUBCOMMAND_REMOVE_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_REMOVE, 1));
		subCommands.add(new SubCommand(SUBCOMMAND_INSERT_FORMAT, SUBCOMMAND_INSERT_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_INSERT, 5));
		subCommands.add(new SubCommand(SUBCOMMAND_INFO_FORMAT, SUBCOMMAND_INFO_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_INFO, 1));
		subCommands.add(new SubCommand(SUBCOMMAND_PRINT_FORMAT, SUBCOMMAND_PRINT_FORMAT_DESCRIPTION, null, 0));
		subCommands.add(new SubCommand(SUBCOMMAND_SETGENRE_FORMAT, SUBCOMMAND_SET_GENRE_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_SET_GENRE, 2));
		subCommands.add(new SubCommand(SUBCOMMAND_SET_FORMAT, SUBCOMMAND_SET_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_SET, 3));
		subCommand = new SubCommand();
	}
    
	private enum CommandKeys{
		r,a,i,p,g,s;
	}
	
	@Override
	public boolean execute(String... args) {
		try{
			subCommand.getSubCommand(args, subCommands);
			String[] arguments = subCommand.getArguments();
			switch(CommandKeys.valueOf(subCommand.getKey())){
			case r:
				ms.removeRecord(arguments[0]);
				break;
			case a:
				ms.insertTrack(arguments);
				break;
			case i:
				ms.printTrackInfo(arguments[0]);
				break;
			case p:
				ms.printAllTracksTitle();
				break;
			case g:
				ms.moveRecordAnotherSet(arguments[0], arguments[1]);
				break;
			case s:
				for(int i=0;i<args.length;i++){
					if (arguments[i].equalsIgnoreCase(Field.LENGTH.name()))
						arguments[i] = Field.LENGTH.replacement;
					if (arguments[i].equalsIgnoreCase(Field.TITLE.name()))
						arguments[i] = Field.TITLE.replacement;
				}
				ms.setTrack(arguments[0], arguments);
				break;
			}
			} catch (IllegalArgumentException e){
			throw new IllegalArgumentException(e.getMessage());
			}
			return true;
	}

    @Override
    public void printHelp() {
    	for(SubCommand sc: subCommands)
			ms.doEvent(new HelpContainer(sc.getFormat(), sc.getFormatDescription()));
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }

    @Override
    public String getDescription() {
        return COMMAND_DESCRIPTION;
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