package init;

/**
 * @author Ksiona
 * version 2.0
 */

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import management.ManagementSystem;

import org.apache.log4j.Logger;

import output.DisplaySystem;
import commands.CommandProcessor;

public class InformationSystem {
	
	private static final String START = "Start application";
	private static final String WELCOME_MESSAGE = "Welcome to the information system \"Music Library\" \r\n"
			+ "To get instructions on how to use enter command \"help /\"";
	private static final String CONSOLE_ENCODING = "console.encoding";
	public static final String CONSOLE_ENCODING_VALUE = "Cp866";
	private static final String FILE_ENCODING = "file.encoding";
	private static final String FILE_ENCODING_VALUE = "UTF-8";
	private static final String WARNING_ENCODING = "Unsupported encoding set for console, the application will be closed, please contact support ";
	private static final String WARNING_UNHANDLE = "A critical error has occurred, the application will be closed, please contact support";
	private static DisplaySystem ds;
    private static final Logger rlog = Logger.getRootLogger();
    private static final Logger log = Logger.getLogger(InformationSystem.class);
    
    public InformationSystem() {
    	this.ds = DisplaySystem.getInstance();
    	ManagementSystem.getInstance();
		initCommandProcessor();
	}

	private void initCommandProcessor() {
    	try {
        	System.setProperty(FILE_ENCODING, FILE_ENCODING_VALUE);
        	System.setProperty(CONSOLE_ENCODING, CONSOLE_ENCODING_VALUE);
    		System.setOut(new PrintStream(System.out, true, CONSOLE_ENCODING_VALUE));
        	ds.DisplayMessage(WELCOME_MESSAGE);
        	CommandProcessor cp = CommandProcessor.getInstance(CONSOLE_ENCODING_VALUE);
        	cp.execute();
    	} catch (UnsupportedEncodingException ex) {
    		ds.DisplayMessage(WARNING_ENCODING);
    		rlog.fatal(ex.getMessage(), ex);
    	} catch (Throwable e){
    		ds.DisplayMessage(WARNING_UNHANDLE);
    		rlog.fatal(e.getMessage(), e);;
    	}
	}

	public static void main(String[] args){
		log.info(START);
		new InformationSystem();
    }

}
