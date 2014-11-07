package commands;

import interfaces.Command;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import output.DisplaySystem;
 
/**
 * @author Ksiona
 * CommandProcessor
 */
public class CommandProcessor {
	 
    private Map<String, Command> commands;
	private static DisplaySystem ds;
    private String consoleEncoding;
 
    public CommandProcessor(DisplaySystem ds, String consoleEncoding) {
        commands = new TreeMap<>();
        Command cmd = new HelpCommand(commands);//Command that contains all commands and also itself? Not cool, not at all
        commands.put(cmd.getName(), cmd);//copy
        cmd = new TrackCommand();//and paste
        commands.put(cmd.getName(), cmd);//and copy
        cmd = new GenreCommand();//and paste
        commands.put(cmd.getName(), cmd);//it makes me sad
        putCommandIntoMap(new SearchCommand()); //that's way shorter, ain't it?
        
        cmd = new ExitCommand();
        commands.put(cmd.getName(), cmd);
        this.consoleEncoding = consoleEncoding;
        this.ds = DisplaySystem.getInstance();
    }
    
    private void putCommandIntoMap(Command c, Map<String, Command> map)
    {
    	map.put(c.getName(), c);
    }
 
    public void execute() {
        boolean result = true;
        Scanner scanner = new Scanner(System.in, consoleEncoding);
        do {
        	
        	ds.DisplaySymbols("> ");//TODO: No magic symbols. Move them to consts.
            String fullCommand = scanner.nextLine();
            if (fullCommand == null || "".equals(fullCommand)) {
                continue;
            }
            ParsedCommand parser = new ParsedCommand(fullCommand);
            if (parser.command == null || "".equals(parser.command)) {
                continue;
            }
            Command cmd = commands.get(parser.command.toUpperCase());
            if (cmd == null) {
            	ds.DisplayMessage("Command not found");//Same here
                continue;
            }
            result = cmd.execute(parser.args);
        } while (result);
    }
 
    private class ParsedCommand {
        String command;
        String[] args;
 
        public ParsedCommand(String line) {
        	List<String> result = new ArrayList<>();
        	Pattern pattern = Pattern.compile("((\\\"[^\"]+\\\")|\\S+)");//TODO: all magic must go into constants!
    		Matcher matcher = pattern.matcher(line);
    		while (matcher.find())
    		   result.add(matcher.group().replaceAll("\"",""));
    	
            if (result != null) {
                command = result.get(0);
                if (result.size() > 1) {
                	result.remove(0);
                    args = result.toArray(new String[0]);                  
                }
            }
        }
    }
}
