package commands;

import java.util.List;

public class SubCommand{
	
	private static final String WARNING_NO_COMMAND_PARAMETER = "You must specify the parameter. Type \"help\" with command, to view available";
	private static final String DASH = "-";
	private static final String EMPTY_STRING = "";
	private String key;
	private String format;
	private String formatDescription;
	private String warning;
	private int methodParametersQuantity;
	private SubCommand subCommand;
	private String[] arguments;
	
	public SubCommand(String format, String formatDescription, String warning, int methodParametersQuantity) {
		this.format = format;
		this.formatDescription = formatDescription;
		this.warning = warning;
		this.methodParametersQuantity = methodParametersQuantity;
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
			key = args[0].replaceAll(DASH, EMPTY_STRING);
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

	public String[] getArguments() {
		return arguments;
	}
}