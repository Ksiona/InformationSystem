package commands;

import interfaces.Command;

import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;


import org.apache.log4j.Logger;

import output.DisplaySystem;
 
/**
 * @author Ksiona
 * CommandProcessor
 */
public class CommandProcessor {
	 
	private static final String INVITATION_TO_PRINT = "> ";
	private static final Logger log = Logger.getLogger(CommandProcessor.class);
    private Map<String, Command> commands;
	private static DisplaySystem ds;
    private static String consoleEncoding;
    private Scanner scanner;
    private CommandParser parser;
    
  
    private CommandProcessor() {
        this.ds = DisplaySystem.getInstance();
        commands = new TreeMap<>();
        putCommandIntoMap(new HelpCommand(commands), commands);
        putCommandIntoMap(new TrackCommand(), commands);
        putCommandIntoMap(new GenreCommand(), commands);
        putCommandIntoMap(new SearchCommand(), commands);
        putCommandIntoMap(new ExitCommand(), commands);
        this.scanner = new Scanner(System.in, consoleEncoding);
        
    }
    
	private static class SingletonHolder {
		private static final CommandProcessor INSTANCE = new CommandProcessor();
	}
	
	public static CommandProcessor getInstance(String consoleEncoding) {
		CommandProcessor.consoleEncoding = consoleEncoding;
		return SingletonHolder.INSTANCE;
	}
 
    private void putCommandIntoMap(Command c, Map<String, Command> map){
    	map.put(c.getName(), c);
    }
    
    public void execute() {
    	try{
	        boolean result = true; 
	        do {
	        	ds.DisplaySymbols(INVITATION_TO_PRINT);
	        	String fullCommand = "";
	        	String line;
	        	do  {
	        		line = scanner.nextLine();
	        		 fullCommand += line;
	        	}while (!line.contains("/"));
	            if (fullCommand == null || "".equals(fullCommand)) {
	                continue;
	            }
	            parser = new CommandParser(fullCommand);
	            if (parser.command == null || "".equals(parser.command)) {
	                continue;
	            }
	            Command cmd = commands.get(parser.command.toUpperCase());
	            if (cmd == null) {
	            	ds.DisplayMessage(Command.COMMAND_NOT_FOUND);
	                continue;
	            }
	            result = cmd.execute(parser.args);
	            
	        } while (result);
    	}catch(RuntimeException e){
    		ds.DisplayError(e);
    		log.warn(e.getMessage(), e);
    	}finally{
    		scanner.close();
    	}
    }
}