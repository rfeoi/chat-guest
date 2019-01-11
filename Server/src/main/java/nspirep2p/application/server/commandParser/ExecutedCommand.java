package nspirep2p.application.server.commandParser;

/**
 * This is a class with all Information of a ExecutedCommand
 * Created by strifel on 14.12.2018.
 */
public class ExecutedCommand {

    private String error = "";
    private boolean correctCommand = true;
    private Command command;
    private String[] args;

    ExecutedCommand(String unparsed) {
        if (unparsed.contains(" ")) {
            String[] splitCommand = unparsed.split(" ");
            try {
                command = Command.valueOf(splitCommand[0].toUpperCase());
            } catch (EnumConstantNotPresentException e) {
                correctCommand = false;
                error = "NOTEXISTS";
                return;
            }
        } else {
            try {
                command = Command.valueOf(unparsed.toUpperCase());
            } catch (EnumConstantNotPresentException e) {
                correctCommand = false;
                return;
            }
        }
    }

    public boolean isCorrect() {
        return correctCommand;
    }

    public String getError() {
        return error;
    }
}
