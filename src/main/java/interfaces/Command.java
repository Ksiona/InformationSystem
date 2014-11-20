package interfaces;

public interface Command {

    static final String COMMAND_NOT_FOUND = "Command not found";
    static final String LINE_DELIMITER = "========================================================================================================================";
    boolean execute(String... args);
    void printHelp();
    String getName();
    String getDescription();
}
