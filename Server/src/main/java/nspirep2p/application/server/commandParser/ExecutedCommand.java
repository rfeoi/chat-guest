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
            } catch (IllegalArgumentException e) {
                correctCommand = false;
                error = "NOTEXISTS";
                return;
            }
            args = new String[splitCommand.length - 1];
            System.arraycopy(splitCommand, 1, args, 0, splitCommand.length - 1);
            if (command.minParameter > args.length || command.maxParameter < args.length){
                error = "TOOLESSPARAMETER";
            }
        } else {
            try {
                command = Command.valueOf(unparsed.toUpperCase());
            } catch (IllegalArgumentException e) {
                correctCommand = false;
                return;
            }
        }
        if (command.getParent() != null){
            command = command.getParent();
        }
    }

    public boolean isCorrect() {
        return correctCommand;
    }

    public String getError() {
        return error;
    }

    public Command getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
}
