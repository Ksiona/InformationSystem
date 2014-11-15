package commands;

import interfaces.Command;

import java.util.List;
import java.util.Map;

import commands.CommandProcessor.CommandLoader;
import interfaces.Listener;
import output.DisplaySystem;

class HelpCommand implements Command {
	
	private static final String COMMAND_DESCRIPTION = "Prints list of available commands";
	private static final String COMMAND_NAME = "HELP";
	private static final String NEW_LINE = "\n";
	private static final String COLON = ": ";
	private static final String INFO_MESSAGE_HELP = "Help for command ";
	private static final String INFO_MESSAGE_AVAILABLE = "Available commands:\n";
	private static final String EXECUTION_SYMBOL_DESCRIBER = "\r\nAfter each full command, you must enter the symbol \"/\" for command execution";
	private List<CommandLoader<?>> commands;
	private Listener ds;
	
	public HelpCommand(List<CommandLoader<?>> commands) {
		this.commands = commands;
		this.ds = DisplaySystem.getInstance();
	}
	 
    @Override
    public boolean execute(String... args) {
  	 	if (args == null) {
			ds.doEvent(INFO_MESSAGE_AVAILABLE + LINE_DELIMITER);
			for (CommandLoader<?> cl : commands) {
				Command cmd = (Command) cl.getInstance();
				ds.doEvent(cmd.getName() + COLON + cmd.getDescription());
			}
			ds.doEvent(LINE_DELIMITER);
			ds.doEvent(EXECUTION_SYMBOL_DESCRIBER);
		}else {
			boolean isFinded = false;
			for (CommandLoader<?> cl : commands) {
				Command cmd = (Command) cl.getInstance();
					if(cmd.getName().equalsIgnoreCase(args[0])){
						ds.doEvent(INFO_MESSAGE_HELP + args[0] + COLON + NEW_LINE + LINE_DELIMITER);
						cmd.printHelp();
						ds.doEvent(LINE_DELIMITER);
						isFinded = true;
						break;
					}
			}
			if (!isFinded) {
				ds.doEvent(COMMAND_NOT_FOUND);
			} 
		}
        return true;
    }

    @Override
    public void printHelp() {
    	ds.doEvent(getDescription());
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
