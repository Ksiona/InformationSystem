package commands;

import interfaces.Command;
import interfaces.Listener;
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
	private Listener ds;
	
    public ExitCommand() {
		this.ds = DisplaySystem.getInstance();
		this.ms = ManagementSystem.getInstance();
	}
    
	private static class SingletonHolder {
		private static final ExitCommand INSTANCE = new ExitCommand();
	}
	
	public static ExitCommand getInstance() {
		return SingletonHolder.INSTANCE;
	}
	
    @Override
    public boolean execute(String... args) {
    	ms.writeUnsavedChanges();
    	ds.doEvent(EXIT_MESSAGE);
    	log.info(EXIT);
        return false;
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
