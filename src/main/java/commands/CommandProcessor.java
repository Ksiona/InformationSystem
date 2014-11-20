package commands;

import interfaces.Command;
import interfaces.Listener;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import management.ManagementSystem;

import org.apache.log4j.Logger;
 
/**
 * @author Ksiona
 * CommandProcessor
 */
public class CommandProcessor {
	 
	private static final String INVITATION_TO_PRINT = "> ";
	private static final String EMPTY_STRING = "";
	private static final String KEY_COMMAND_END = "/";
	private static final Logger log = Logger.getLogger(CommandProcessor.class);
	private static final String MULTILINE_MODE_INPUT_CONDITION = "track -a";
    private List<CommandLoader<?>> commands;
	private static Listener ms;
    private static String consoleEncoding;
    private Scanner scanner;
    private CommandParser parser;
    private HelpCommand hp;
    
  
    private CommandProcessor() {
        CommandProcessor.ms = ManagementSystem.getInstance();
        commands = new ArrayList<>();
        commands.add(new CommandLoader<>(TrackCommand.class));
		commands.add(new CommandLoader<>(GenreCommand.class));
		commands.add(new CommandLoader<>(SearchCommand.class));
		commands.add(new CommandLoader<>(ExitCommand.class));
		this.hp = new HelpCommand(commands);
        this.scanner = new Scanner(System.in, consoleEncoding);
        this.parser = new CommandParser();
    }
    
    class CommandLoader<T extends Command> {
        Class<? extends T> commandClass;
     
        public CommandLoader(Class<? extends T>  commandClass) {
            this.commandClass = commandClass;
        }
     
        public T getInstance() {
        	T instance = null;
			try {
				instance = commandClass.newInstance();
			} catch (InstantiationException | IllegalAccessException e) {
				ms.doEvent(e);
			}
			return instance;
        }
    }
    
	private static class SingletonHolder {
		private static final CommandProcessor INSTANCE = new CommandProcessor();
	}
	
	public static CommandProcessor getInstance(String consoleEncoding) {
		CommandProcessor.consoleEncoding = consoleEncoding;
		return SingletonHolder.INSTANCE;
	}
 
    public void execute() {
    	try{
	        boolean result = true; 
	        do {
	        	ms.doEvent(INVITATION_TO_PRINT);
		        boolean isFinded = false;
	        	String fullCommand = EMPTY_STRING;
	        	String line = scanner.nextLine();
	        	if (line.contains(MULTILINE_MODE_INPUT_CONDITION))
		        	do  {
		        		fullCommand += line;
		        		line = scanner.nextLine();
		        	}while (!line.contains(KEY_COMMAND_END));
	        	else
	        		fullCommand = line;
	           
	        	if (fullCommand == null || KEY_COMMAND_END.equals(fullCommand) || EMPTY_STRING.equals(fullCommand)) {
	                continue;
	            }
	            parser.parse(fullCommand);
	            if (parser.command == null || EMPTY_STRING.equals(parser.command)) {
	                continue;
	            }
	            if (parser.command.equalsIgnoreCase(hp.getName())){
	            	result = hp.execute(parser.args);
	            	isFinded = true;
	            }else
		            for(CommandLoader<?> cl:commands){
		            	Command cmd = (Command)cl.getInstance();
		            	if(cmd.getName().equalsIgnoreCase(parser.command)){
		            		result = cmd.execute(parser.args);
		            		isFinded = true;
		            		break;
		            	}
		            }
		            if(!isFinded)
		            	ms.doEvent(Command.COMMAND_NOT_FOUND);
          
	        } while (result);
    	}catch(RuntimeException e){
    		ms.doEvent(e);
    		log.warn(e.getMessage(), e);
    		parser.args = null;
    		execute();
    	}
    }
}