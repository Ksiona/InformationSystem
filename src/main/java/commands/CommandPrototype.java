package commands;

public class CommandPrototype{
	
	private String key;
	private String format;
	private String formatDescription;
	private String warning;
	private int methodParametersQuantity;
	
	public CommandPrototype(String format, String formatDescription, String warning, int methodParametersQuantity) {
		this.format = format;
		this.formatDescription = formatDescription;
		this.warning = warning;
		this.methodParametersQuantity = methodParametersQuantity;
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

	public String getKey() {
		return format.substring(1,3).trim();
	}
}