package nspirep2p.application.server.commandParser;

/**
 * This defines all valid commands
 * Created by strifel on 14.12.2018.
 */
public enum Command {
    STOP(null, 0, 0, "stop"),
    CLOSE(STOP, 0, 0, "close"),
    EXIT(STOP, 0, 0, "exit"),
    BREAK(STOP, 0, 0, "break"),
    CCHANEL(null, 2, 2, "cchanel <name> <level>");


    Command parent;
    int minParameter;
    int maxParameter;
    String usage;

    /**
     * This defines a command
     *
     * @param parent       the parent is the command which will executed instead of the typed command
     * @param minParameter the minimum of parameter a method must have
     * @param maxParameter the maximum parameter a command can have
     * @param usage        the usage which will be displayed if the command parameters a re not enough/too much
     */
    Command(Command parent, int minParameter, int maxParameter, String usage) {
        this.parent = parent;
        this.minParameter = minParameter;
        this.maxParameter = maxParameter;
        this.usage = usage;
    }

    public Command getParent() {
        return parent;
    }

    public int getMinParameter() {
        return minParameter;
    }

    public int getMaxParameter() {
        return maxParameter;
    }

    public String getUsage() {
        return usage;
    }
}
