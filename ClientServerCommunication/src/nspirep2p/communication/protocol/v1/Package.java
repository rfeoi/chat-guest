package nspirep2p.communication.protocol.v1;

import java.util.HashMap;

/**
 * Class used to identify packages in communication
 * <p>
 * Used by parse function in CommunicationParser
 * Created by strifel on 12.11.2018.
 */
public class Package {
    private Function function;
    private String auth;
    private HashMap<String, String> args;
    private boolean waitForAnswer;

    Package(Function function) {
        this.function = function;
        args = new HashMap<>();
    }

    void authenticateUser(String uuid) {
        this.auth = uuid;
    }

    void setWaitForAnswer(boolean waiting) {
        waitForAnswer = waiting;
    }

    void addArg(String arg, String value) {
        args.put(arg, value);
    }

    public Function getFunction() {
        return function;
    }

    public String getAuthUUID() {
        return auth;
    }

    public String getArg(String argument) {
        return args.get(argument);
    }

    public boolean isWaitForAnswer() {
        return waitForAnswer;
    }
}
