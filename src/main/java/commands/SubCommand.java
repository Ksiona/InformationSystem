package commands;

import java.util.List;

import management.ManagementSystem;
import output.HelpContainer;
import interfaces.Listener;

public class SubCommand{
	
	private static final String WARNING_NO_COMMAND_PARAMETER = "You must specify the parameter. Type \"help\" with command, to view available";
	private String key;
	private String format;
	private String formatDescription;
	private String warning;
	private int methodParametersQuantity;
	private SubCommand subCommand;
	private String[] arguments;
	private static Listener listener;
	
	public SubCommand(String format, String formatDescription, String warning, int methodParametersQuantity) {
		this.format = format;
		this.formatDescription = formatDescription;
		this.warning = warning;
		this.methodParametersQuantity = methodParametersQuantity;
		this.listener = ManagementSystem.getInstance();
	}
	public SubCommand() {
	}

	public String getFormat() {
		return format;
	}

	public String getFormatDescription() {
		return formatDescription;
	}

	public String getWarning() {
		return warning;
	}

	public int getMethodParametersQuantity() {
		return methodParametersQuantity;
	}

	public String getCommandKey() {
		return format.substring(1,3).trim();
	}
	
	public String getKey() {
		return key;
	}
	
	public void getSubCommand(String[] args, List<SubCommand> subCommands) {
		if (args == null) 
			throw new IndexOutOfBoundsException(WARNING_NO_COMMAND_PARAMETER);
		else try{
			key = args[0].replaceAll("-", "");
			
			for(SubCommand cp : subCommands)
				if (cp.getCommandKey().equalsIgnoreCase(key))
					subCommand = cp;
			
			arguments = new String [args.length-1];
			System.arraycopy(args, 1, arguments, 0, args.length-1);
			if((arguments.length) < subCommand.getMethodParametersQuantity())
				throw new IndexOutOfBoundsException(subCommand.getWarning());
		} catch (NullPointerException e){
			throw new IllegalArgumentException(WARNING_NO_COMMAND_PARAMETER);
		}
	}
	public static void printHelp(List<SubCommand> subCommands) {
		for(SubCommand sc: subCommands)
			listener.doEvent(new HelpContainer(sc.getFormat(), sc.getFormatDescription()));
	}
}