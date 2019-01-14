package nspirep2p.application.server.commandParser;

import java.util.ArrayList;

/**
 * This is a class used to parse Command
 * Created by strifel on 14.12.2018.
 */
public class CommandParser {
    private ArrayList<ExecutedCommand> commandHistory;

    public CommandParser() {

    }

    public void insertManually(String insertable){
        commandHistory.add(new ExecutedCommand(insertable));
    }

    public ExecutedCommand getLastExecuted() {
        return commandHistory.get(commandHistory.size() - 1);
    }

    public boolean wasLastCorrect() {
        return getLastExecuted().isCorrect();
    }


}
