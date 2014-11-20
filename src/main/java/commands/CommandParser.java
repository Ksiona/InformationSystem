package commands;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandParser {
	
	private static final String REGEXP_SKIP_SPACES_IN_QUOTS_AND_UNRESOLVED_SYMBOLS = "((\\\"[^\"]+\\\")|([\\w-*?\\.])+)";
	private static final String REGEXP_FOR_SELECTION = "Y|N|S";
	private static final String SHIELDED_QUOTE = "\"";
	private static final String EMPTY_STRING = "";
	protected String[] output;
    protected String command;
    protected String[] args;
    
    public void parse(String line){
    	List<String> result = new ArrayList<>();
    	Pattern pattern = Pattern.compile(REGEXP_SKIP_SPACES_IN_QUOTS_AND_UNRESOLVED_SYMBOLS);
		Matcher matcher = pattern.matcher(line);
		while (matcher.find()){
			result.add(matcher.group().replaceAll(SHIELDED_QUOTE, EMPTY_STRING));
		}
	
        if (result != null) {
        	output = result.toArray(new String[0]); 
            command = result.get(0);
            if (result.size() > 1) {
            	result.remove(0);
                args = result.toArray(new String[0]);                  
            }
        }
    }

	public void parseSelection(String line) {
		String result = null;
		Pattern pattern = Pattern.compile(REGEXP_FOR_SELECTION);
		Matcher matcher = pattern.matcher(line);
		if( matcher.find())
			result = matcher.group();
		command = result;	
	}
}