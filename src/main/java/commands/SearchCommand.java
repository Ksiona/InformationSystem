package commands;

import java.util.ArrayList;
import java.util.List;

import interfaces.Command;
import management.ManagementSystem;
import output.HelpContainer;

/**
 * Created by Morthanion on 06.11.2014.
 */
public class SearchCommand implements Command{

	private static final String COMMAND_DESCRIPTION = "Defines operations with genre list";
	private static final String COMMAND_NAME = "SEARCH";
	private static final String WARNING_SUBCOMMAND = "Enter <search mask> to process";
	private static final String SUBCOMMAND_TITLE_FORMAT = "-t  <search mask>";
	private static final String SUBCOMMAND_TITLE_FORMAT_DESCRIPTION = "search by title";
	private static final String SUBCOMMAND_ALBUM_FORMAT = "-a  <search mask>";
	private static final String SUBCOMMAND_ALBUM_FORMAT_DESCRIPTION = "search by album name";
	private static final String SUBCOMMAND_GENRE_FORMAT = "-g  <search mask>";
	private static final String SUBCOMMAND_GENRE_FORMAT_DESCRIPTION = "search by genre";
	private static final String SUBCOMMAND_SINGER_FORMAT = "-s  <search mask>";
	private static final String SUBCOMMAND_SINGER_FORMAT_DESCRIPTION = "search by singer";
	private static final String SUBCOMMAND_LENGTH_FORMAT = "-l  <search mask>";
	private static final String SUBCOMMAND_LENGTH_FORMAT_DESCRIPTION = "search by length";
	private static final String MASK_DESCRIPTION = "search is case-insensitive \r\n" +
			"search mask example \" *m?3 \"\r\n"+
			"where \"*\" - any or none symbols\r\n"+
			"      \"?\" - any or none single symbol\r\n";
    private static  ManagementSystem ms;
    private static List<SubCommand> subCommands;
    private SubCommand subCommand;
    
    public SearchCommand()
    {
    SearchCommand.ms = ManagementSystem.getInstance();
    subCommands = new ArrayList<>();
    subCommands.add(new SubCommand(SUBCOMMAND_TITLE_FORMAT, SUBCOMMAND_TITLE_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND, 1));
    subCommands.add(new SubCommand(SUBCOMMAND_ALBUM_FORMAT, SUBCOMMAND_ALBUM_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND, 1));
    subCommands.add(new SubCommand(SUBCOMMAND_GENRE_FORMAT, SUBCOMMAND_GENRE_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND, 1));
    subCommands.add(new SubCommand(SUBCOMMAND_SINGER_FORMAT, SUBCOMMAND_SINGER_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND, 1));
    subCommands.add(new SubCommand(SUBCOMMAND_LENGTH_FORMAT, SUBCOMMAND_LENGTH_FORMAT_DESCRIPTION, WARNING_SUBCOMMAND, 1));
    subCommand = new SubCommand();
    }
    
    @Override
    public boolean execute(String... args) {
	    try{
		    subCommand.getSubCommand(args, subCommands);
		    ms.searchItems(subCommand.getKey(), args[1]);
	    } catch (IllegalArgumentException e){
	    	throw new IllegalArgumentException(e.getMessage());
	    }
	    return true;
    }

    @Override
    public void printHelp() {
    	for(SubCommand sc: subCommands)
    		ms.doEvent(new HelpContainer(sc.getFormat(), sc.getFormatDescription()));
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
}
