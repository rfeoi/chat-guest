package nspirep2p.communication.protocol.v1;

/**
 * Functions both can receive: client and server
 *
 * (Parameters are always maximum parameters)
 * Created by strifel on 12.11.2018.
 */
public enum Function {
    CHANGE_USERNAME(new String[]{"username", "newUsername"}),
    CREATE_TEMP_CHANNEL(new String[]{"username"}),
    DELETE_TEMP_CHANNEL(new String[]{"username"}),
    MOVE(new String[]{"username", "channel_name"}),
    INVITE(new String[]{"username"}),
    SEND_MESSAGE(new String[]{"channel","message","username"}),
    GET_CLIENTS(new String[]{"names", "uuid"}),
    GET_CHANNELS(new String[]{"channels","uuid"}),
    SEND_ERROR(new String[]{"error"});

    String[] parameters;

    Function(String[] parameters) {
        this.parameters = parameters;
    }

    public String[] getParameters() {
        return parameters;
    }
}

