package commands;

import java.util.ArrayList;
import java.util.List;

import interfaces.Command;
import management.ManagementSystem;
import output.HelpContainer;

public class GenreCommand implements Command {

	private static final String COMMAND_DESCRIPTION = "Defines operations with genres";
	private static final String COMMAND_NAME = "GENRE";
	private static final String WARNING_SUBCOMMAND_SET = "Enter the old name of the genre and new to continue";
	private static final String WARNING_SUBCOMMAND_COMBINE = "The option \"combine genres\" need parameters <genre1 name> <genre2 name> <new genre name>";
	private static final String WARNING_SUBCOMMAND_INSERT = "Enter genre name to add";
	private static final String WARNING_SUBCOMMAND_REMOVE = "Enter genre name to remove";
	private static final String WARNING_SUBCOMMAND_PRINT_TRACKS = "Enter genre name for print it tracks";
	private static final String SUBCOMMAND_REMOVE_FORMAT = "-r <genre name>";
	private static final String SUBCOMMAND_REMOVE_FORMAT_DESCRIPTION = "remove genre from list";
	private static final String SUBCOMMAND_INSERT_FORMAT = "-a <new genre name>";
	private static final String SUBCOMMAND_INSERT_FORMAT_DESCRIPTION = "add genre into list";
	private static final String SUBCOMMAND_COMBINE_FORMAT = "-c <genre1 name> <genre2 name> <new genre name>";
	private static final String SUBCOMMAND_COMBINE_FORMAT_DESCRIPTION = "combines tracks of genre1 and genre2 into new genre name pack";
	private static final String SUBCOMMAND_PRINT_FORMAT = "-p ";
	private static final String SUBCOMMAND_PRINT_FORMAT_DESCRIPTION = "print genre list";
	private static final String SUBCOMMAND_PRINT_TRACKS_FORMAT = "-pt <genre name>";
	private static final String SUBCOMMAND_PRINT_TRACKS_FORMAT_DESCRIPTION = "print tracks list of this genre";
	private static final String SUBCOMMAND_SET_FORMAT = "-s <old genre name> <new genre name>";
	private static final String SUBCOMMAND_SET_FORMAT_DESCRIPTION = "set new genre name";
	private List<SubCommand> subCommands;
	private SubCommand subCommand;
	
	private static ManagementSystem ms;
	
    public GenreCommand() {
		GenreCommand.ms = ManagementSystem.getInstance();
		subCommands = new ArrayList<>();
		subCommands.add(new SubCommand(SUBCOMMAND_REMOVE_FORMAT, SUBCOMMAND_REMOVE_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_REMOVE, 1));
		subCommands.add(new SubCommand(SUBCOMMAND_INSERT_FORMAT, SUBCOMMAND_INSERT_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_INSERT, 1));
		subCommands.add(new SubCommand(SUBCOMMAND_COMBINE_FORMAT, SUBCOMMAND_COMBINE_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_COMBINE, 3));
		subCommands.add(new SubCommand(SUBCOMMAND_PRINT_FORMAT, SUBCOMMAND_PRINT_FORMAT_DESCRIPTION, null, 0));
		subCommands.add(new SubCommand(SUBCOMMAND_PRINT_TRACKS_FORMAT, SUBCOMMAND_PRINT_TRACKS_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_PRINT_TRACKS, 1));
		subCommands.add(new SubCommand(SUBCOMMAND_SET_FORMAT, SUBCOMMAND_SET_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND_SET, 2));
		subCommand = new SubCommand();
	}
	
	private enum CommandKeys{
		r,a,c,p,pt,s;
	}
	
	@Override
	public boolean execute(String... args) {
		try{
			subCommand.getSubCommand(args, subCommands);
			switch(CommandKeys.valueOf(subCommand.getKey())){
			case r:
				ms.removeRecordsList(args[1]);
				break;
			case a:
				ms.insertRecordsList(args[1]);
				break;
			case c:
				ms.combineRecordsLists(args[1], args[2], args[3]);
				break;
			case p:
				ms.getRecordsListsName();
				break;
			case pt:
				ms.getTracksTitles(args[1]);
				break;
			case s:
				ms.setRecordsListName(args[1], args[2]);
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
}
