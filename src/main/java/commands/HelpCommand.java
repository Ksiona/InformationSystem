package commands;

import interfaces.Listener;

import java.util.List;

import management.ManagementSystem;

class HelpCommand extends GenericCommand {
	
	private static final String COMMAND_DESCRIPTION = "Prints list of available commands";
	private static final String COMMAND_NAME = "HELP";
	private static final String NEW_LINE = "\n";
	private static final String COLON = ": ";
	private static final String INFO_MESSAGE_HELP = "Help for command ";
	private static final String INFO_MESSAGE_AVAILABLE = "Available commands:\n";
	private static final String EXECUTION_SYMBOL_DESCRIBER = "\r\nSome commands must be terminated by a character \"/\" for command execution";
	private List<GenericCommand> commands;
	private Listener ms;
	
	public HelpCommand(List<GenericCommand> commands2) {
		this.commands = commands2;
		this.ms = ManagementSystem.getInstance();
	}
	 
    @Override
    public boolean execute(String... args) {
  	 	if (args == null) {
			ms.doEvent(INFO_MESSAGE_AVAILABLE + LINE_DELIMITER);
			for (GenericCommand cl : commands) {
				ms.doEvent(cl.getName() + COLON + cl.getDescription());
			}
			ms.doEvent(LINE_DELIMITER);
			ms.doEvent(EXECUTION_SYMBOL_DESCRIBER);
		}else {
			boolean isFinded = false;
			for (GenericCommand cmd : commands) {
					if(cmd.getName().equalsIgnoreCase(args[0])){
						ms.doEvent(INFO_MESSAGE_HELP + args[0] + COLON + NEW_LINE + LINE_DELIMITER);
						cmd.printHelp();
						ms.doEvent(LINE_DELIMITER);
						isFinded = true;
						break;
					}
			}
			if (!isFinded) {
				ms.doEvent(COMMAND_NOT_FOUND);
			} 
		}
        return true;
    }

    @Override
    public void printHelp() {
    	ms.doEvent(getDescription());
    }

    @Override
    public String getName() {
        return COMMAND_NAME;
    }
}
