package commands;

import interfaces.Listener;

import java.util.Scanner;

import output.StringContainer;
import management.ManagementSystem;

public class SelectionProcessor {
	
	private static final String REMOVE_PARAMETERS_DESCRIPTION = "Enter singer and album for continue";
	private static final String INVITATION_TO_PRINT = ">>";
	private Listener ms;
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
	        	ms.doEvent(new StringContainer(INVITATION_TO_PRINT));
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
	        	ms.doEvent(new StringContainer(INVITATION_TO_PRINT));
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
