package nspirep2p.application.server.commandParser;

/**
 * This defines all valid commands
 * Created by strifel on 14.12.2018.
 */
public enum Command {
    //Commands
    HELP(0, 1, "help (<command>)", "Shows help of all/one commands"),
    STOP(0, 0, "stop", "Stops the server"),
    CCHANNEL(2, 2, "cchanel <name> <level>", "Creates a new channel."),
    LISTCLIENTS(0, 0, "listclients", "Gives back a list of clients"),
    KICK(1, 2, "kick <uuid> (<reason>)", "Kicks a client"),
    KICKALL(0, 1, "kickall (<reason>)", "Kicks all clients"),
    CHANGEUSERNAME(2, 2, "changeUsername <uuid> <newUsername>", "Changes the Name of an client (Can cause client to crash! Use carefully!)"),
    ALERTALL(1, 1, "alertall <message>", "Alert all clients"),
    RENAMECHANNEL(2, 2, "renamechannel <oldName> <newName>", "Renames a channel"),
    SHOWUSERNAME(1, 1, "showusername <uuid>", "Shows username of client"),
    SHOWUUID(1, 1, "showuuid <username>", "Shows uuid of client (Please keep in mind, that this is not possible if the username has whitespaces)"),
    BAN(1, 2, "ban <uuid> (<duration>)", "Ban a user!"),
    UNBAN(1, 1, "unban <uuid>", "Unban a user!"),
    ADDPERM(2, 2, "addperm <group> <permission>", "Add a permission to a group"),
    REMOVEPERM(2, 2, "removeperm <group> <permission>", "Removes a permission of a group"),

    //Aliases
    RCHANNEL(RENAMECHANNEL, 2, 2),
    CLOSE(STOP, 0, 0),
    EXIT(STOP, 0, 0),
    BREAK(STOP, 0, 0),
    USERNAME(CHANGEUSERNAME, 2, 2),
    LIST(LISTCLIENTS, 0, 0);

    Command parent;
    int minParameter;
    int maxParameter;
    String usage;
    String help;

    /**
     * This defines a command
     *
     * @param parent       the parent is the command which will executed instead of the typed command
     * @param minParameter the minimum of parameter a method must have
     * @param maxParameter the maximum parameter a command can have
     */
    Command(Command parent, int minParameter, int maxParameter) {
        this.parent = parent;
        this.minParameter = minParameter;
        this.maxParameter = maxParameter;
        this.usage = parent.usage;
        this.help = null;
    }

    /**
     * This defines a command
     *
     * @param minParameter the minimum of parameter a method must have
     * @param maxParameter the maximum parameter a command can have
     * @param usage        the usage which will be displayed if the command parameters a re not enough/too much
     * @param help         help message
     */
    Command(int minParameter, int maxParameter, String usage, String help) {
        this.parent = null;
        this.minParameter = minParameter;
        this.maxParameter = maxParameter;
        this.usage = usage;
        this.help = help;
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

    public String getHelp() {
        if (help == null) return null;
        return usage + ": " + help;
    }
}
