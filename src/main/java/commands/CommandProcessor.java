package commands;

import interfaces.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import output.DisplaySystem;
 
/**
 * @author Ksiona
 * CommandProcessor
 */
public class CommandProcessor {
	 
	private static final String INVITATION_TO_PRINT = "> ";
	private static final String EMPTY_STRING = "";
	private static final String KEY_COMMAND_END = "/";
	private static final Logger log = Logger.getLogger(CommandProcessor.class);
    private List<CommandLoader<?>> commands;
	private static DisplaySystem ds;
    private static String consoleEncoding;
    private Scanner scanner;
    private CommandParser parser;
    private HelpCommand hp;
    
  
    private CommandProcessor() {
        this.ds = DisplaySystem.getInstance();
        commands = new ArrayList<>();
        commands.add(new CommandLoader<>(TrackCommand.class));
		commands.add(new CommandLoader<>(GenreCommand.class));
		commands.add(new CommandLoader<>(SearchCommand.class));
		commands.add(new CommandLoader<>(ExitCommand.class));
		this.hp = new HelpCommand(commands);
        this.scanner = new Scanner(System.in, consoleEncoding);
    }
    
    class CommandLoader<T extends Command> {
        Class<? extends T> commandClass;
     
        public CommandLoader(Class<? extends T>  commandClass) {
            this.commandClass = commandClass;
        }
     
        @SuppressWarnings("unchecked")
		T getInstance() {
        	T instance = null;
			try {
	        	Method method = commandClass.getMethod("getInstance", null);
				instance = (T) method.invoke(method, null);
			} catch (IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	//T instance = commandClass.newInstance();
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
		        boolean isFinded = false;
	        	ds.DisplaySymbols(INVITATION_TO_PRINT);
	        	String fullCommand = EMPTY_STRING;
	        	String line;
	        	do  {
	        		line = scanner.nextLine();
	        		fullCommand += line;
	        	}while (!line.contains(KEY_COMMAND_END));
	            if (fullCommand == null || EMPTY_STRING.equals(fullCommand)) {
	                continue;
	            }
	            parser = new CommandParser(fullCommand);
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
		            	ds.DisplayMessage(Command.COMMAND_NOT_FOUND);
          
	        } while (result);
    	}catch(RuntimeException e){
    		ds.DisplayError(e);
    		log.warn(e.getMessage(), e);
    	}finally{
    		scanner.close();
    	}
    }
}