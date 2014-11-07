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
	 
	static final String INVITATION_TO_PRINT = "> ";
	static final String REGEXP_SKIP_SPACES_IN_QUOTS = "((\\\"[^\"]+\\\")|\\S+)";
	static final String SHIELDED_QUOTE = "\"";
	static final String EMPTY_STRING = "";
    private Map<String, Command> commands;
	private static DisplaySystem ds;
    private String consoleEncoding;
 
    public CommandProcessor(DisplaySystem ds, String consoleEncoding) {
        commands = new TreeMap<>();
        putCommandIntoMap(new HelpCommand(commands), commands);
        putCommandIntoMap(new TrackCommand(), commands);
        putCommandIntoMap(new GenreCommand(), commands);
        putCommandIntoMap(new SearchCommand(), commands);
        putCommandIntoMap(new ExitCommand(), commands);
        this.consoleEncoding = consoleEncoding;
        this.ds = DisplaySystem.getInstance();
    }
 
    private void putCommandIntoMap(Command c, Map<String, Command> map){
    	map.put(c.getName(), c);
    }
    
    public void execute() {
        boolean result = true;
        Scanner scanner = new Scanner(System.in, consoleEncoding);
        do {
        	ds.DisplaySymbols(INVITATION_TO_PRINT);//TODO: Same here: too tight a bound with DS. 
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
            	ds.DisplayMessage(Command.COMMAND_NOT_FOUND);
                continue;
            }
            result = cmd.execute(parser.args);
        } while (result);
    }
 
    private class ParsedCommand {
        private String command;
        private String[] args;
 
        public ParsedCommand(String line) {
        	List<String> result = new ArrayList<>();
        	Pattern pattern = Pattern.compile(REGEXP_SKIP_SPACES_IN_QUOTS);
    		Matcher matcher = pattern.matcher(line);
    		while (matcher.find())
    		   result.add(matcher.group().replaceAll(SHIELDED_QUOTE, EMPTY_STRING));
    	
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
