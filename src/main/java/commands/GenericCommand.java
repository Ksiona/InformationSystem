package commands;

import interfaces.Listener;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import management.ManagementSystem;
import output.HelpContainer;

/**
 * Created by Morthanion on 14.11.2014.
 */
public class GenericCommand {
	
	static final String COMMAND_NOT_FOUND = "Command not found";
    static final String LINE_DELIMITER = "========================================================================================================================";
	private static final String COMMAND_NAME = "command_name";
	private static final String COMMAND_DESCRIPTION = "command_description";
	private Listener listener;


    private ManagementSystem ms;
	private Class<? extends GenericCommand> commandClass;
	
    public GenericCommand(Class<? extends GenericCommand>  commandClass) {
        this.commandClass = commandClass;
        this.listener = ManagementSystem.getInstance();
    }
    
    public GenericCommand() {
	}
 
    public void getInstance() {
		try {
			commandClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			ms.doEvent(e);
		}
    }

	public boolean execute(String... args) {
		Class[] paramTypes = new Class[] {String[].class}; 
		boolean retObject = false;
		try {
			Method m = commandClass.getMethod("execute", paramTypes);
			retObject = (boolean) m.invoke(commandClass.newInstance(), new Object[] {args});
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException e) {
			e.printStackTrace();
		} 
		return retObject;
	}

	public void printHelp() {
		List<SubCommand> subCommands = null;
		Field[] fields = commandClass.getDeclaredFields();
    	for(Field temp : fields){
    		temp.setAccessible(true);
    		if(temp.getName().equalsIgnoreCase("subCommands")){

    			try {
    				subCommands = (List<SubCommand>) temp.get(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}

		for(SubCommand sc: subCommands)
			listener.doEvent(new HelpContainer(sc.getFormat(), sc.getFormatDescription()));
	}

	public String getName() {
		return getConstantValue(COMMAND_NAME);
	}

	public String getDescription() {
		return getConstantValue(COMMAND_DESCRIPTION);
	}
	
	public String getConstantValue(String constant){
		String element = null;
		Field[] fields = commandClass.getDeclaredFields();
    	for(Field temp : fields){
    		temp.setAccessible(true);
    		if(temp.getName().equalsIgnoreCase(constant)){
    			try {
					element = (String) temp.get(null);
				} catch (IllegalArgumentException | IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
		return element;
	}
}
