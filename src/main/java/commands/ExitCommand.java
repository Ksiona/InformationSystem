package commands;

import interfaces.Command;
import management.ManagementSystem;

import org.apache.log4j.Logger;

import output.DisplaySystem;

class ExitCommand implements Command {
	
	private static final Object EXIT = "Stop application";
	private static final String COMMAND_DESCRIPTION = "Exits from command processor";
	private static final String COMMAND_NAME = "EXIT";
	private static final String EXIT_MESSAGE = "Finishing command processor... done.";
	private static final Logger log = Logger.getLogger(ExitCommand.class);
	private static ManagementSystem ms;
	private DisplaySystem ds;
	
    public ExitCommand() {
		this.ds = DisplaySystem.getInstance();
		this.ms = ManagementSystem.getInstance();
	}
	
    @Override
    public boolean execute(String... args) {
    	ms.writeUnsavedChanges();
    	ds.DisplayMessage(EXIT_MESSAGE);
    	log.info(EXIT);
        return false;
    }

    @Override
    public void printHelp() {
       ds.DisplayMessage(getDescription());
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
