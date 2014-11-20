package commands;

import java.util.Scanner;

import management.ManagementSystem;

public class SelectionProcessor {
	
	private static final String REMOVE_PARAMETERS_DESCRIPTION = "Enter singer and album for continue";
	private ManagementSystem ms;
	private Scanner scanner;
	private CommandParser parser;
	
	public SelectionProcessor() {
		this.ms = ManagementSystem.getInstance();
		this.scanner = new Scanner(System.in);
        this.parser = new CommandParser();
	}

	 public String askUserChoice(String question) {
	        boolean result = true;
	        do{
	        	ms.doEvent(question);
	        	String fullCommand = scanner.nextLine();
	        	if (fullCommand != null) {
	        		parser.parseSelection(fullCommand.toUpperCase());
	        		if(parser.command != null){
	        			result = false;
	        		}
	            } 
	        } while (result);
			return parser.command;
	 }
	 
	 public String[] removeCaseParameters(){
		 boolean result = true;
	        do{
	        	ms.doEvent(REMOVE_PARAMETERS_DESCRIPTION);
	        	String fullCommand = scanner.nextLine();
	        	if (fullCommand != null) {
	        		parser.parse(fullCommand);
	        		if((parser.output != null) && parser.output.length>1){
	        			result = false;
	        		}
	            } 
	        } while (result);
			return parser.output;
	 }
	 
}
