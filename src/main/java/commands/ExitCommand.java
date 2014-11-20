package commands;

import interfaces.Command;
import management.ManagementSystem;
import org.apache.log4j.Logger;

class ExitCommand implements Command {
	
	private static final Object EXIT = "Stop application";
	private static final String COMMAND_DESCRIPTION = "Exits from command processor";
	private static final String COMMAND_NAME = "EXIT";
	private static final String EXIT_MESSAGE = "Finishing command processor... done.";
	private static final Logger log = Logger.getLogger(ExitCommand.class);
	private static ManagementSystem ms;
	
    public ExitCommand() {
		ExitCommand.ms = ManagementSystem.getInstance();
	}
	
    @Override
    public boolean execute(String... args) {
    	ms.writeUnsavedChanges();
    	ms.doEvent(EXIT_MESSAGE);
    	log.info(EXIT);
        return false;
    }

    @Override
    public void printHelp() {
       ms.doEvent(getDescription());
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
