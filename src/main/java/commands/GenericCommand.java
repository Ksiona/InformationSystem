package commands;

import management.ManagementSystem;
import output.DisplaySystem;

/**
 * Created by Morthanion on 14.11.2014.
 */
public class GenericCommand {
    private static String COMMAND_DESCRIPTION = "";
    private static String COMMAND_NAME = "";

    private ManagementSystem ms;
    private DisplaySystem ds;
    public GenericCommand(){
        this.ms = ManagementSystem.getInstance();
        this.ds = DisplaySystem.getInstance();
    }
    
}
