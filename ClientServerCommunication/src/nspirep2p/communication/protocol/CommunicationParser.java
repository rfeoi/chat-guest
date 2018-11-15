package nspirep2p.communication.protocol;

import java.util.Random;

/**
 * Handles the protocol between client and server
 * Created by strifel on 07.11.2018.
 */
public class CommunicationParser {
    private static final String PROTOCOL_VERSION = "1.0";
    private static final String END_WAIT = "waiting";
    private static final String END_BREAK = "break";
    private ClientType clientType;

    public CommunicationParser(ClientType clientType) {
        this.clientType = clientType;
    }

    /**
     * Handels the handshake beginning for client
     * <p>
     * After Handshake you should get denied or accepted
     * Only accepts clients
     *
     * @return handshake lines
     */
    public String[] doHandshake(Client client) {
        String[] handshake;
        if (clientType == ClientType.CLIENT) {
            client.uuid = new Random().nextInt(Integer.MAX_VALUE) + "";
            handshake = new String[3];
            handshake[0] = "client.nspirep2p.version=" + PROTOCOL_VERSION;
            handshake[1] = "client.nspirep2p.uuid=" + client.uuid;
            handshake[2] = END_WAIT;
            return handshake;
        }
        return null;
    }

    /**
     * Handels parsing of handshake on server side
     * First line of response is either deny or accept
     * This can be used to do Server stuff if denied
     *
     * @param handshake the handshake recieved from client
     * @param newClient the client you want to have the informations in
     * @return protocol to send to client
     */
    public String[] parseClientHandshake(String[] handshake, Client newClient) {
        if (handshake.length == 3) {
            if (handshake[0].contains("=") && handshake[1].contains("=") && !handshake[2].contains("=")) {
                if (handshake[0].startsWith("client.nspirep2p.version=") && handshake[1].startsWith("client.nspirep2p.uuid=") && handshake[2].equals("waiting")) {
                    if (handshake[0].split("=")[1].equals(PROTOCOL_VERSION)) {
                        newClient.uuid = handshake[1].split("=")[1];
                        String[] response = new String[2];
                        response[0] = "accept";
                        response[1] = END_WAIT;
                        return response;
                    }
                }
            }
        }
        String[] response = new String[2];
        response[0] = "deny";
        response[1] = END_BREAK;
        return response;
    }

    /**
     * For both server and client side
     * <p>
     * Client: Change your own name
     * You should get acceptance or deny back
     * Server: Inform clients that a client changed his name
     * You should not get any response
     *
     * @param client   who changes the username
     * @param username the new username
     * @return push commands
     */
    public String[] pushUsername(Client client, String username) {
        String[] push;
        if (clientType == ClientType.CLIENT) {
            push = new String[4];
            push[0] = "function=CHANGE_USERNAME";
            push[1] = "auth.uuid=" + client.uuid;
            push[2] = "client.username=" + username;
            push[3] = END_WAIT;
            return push;
        } else if (clientType == ClientType.SERVER) {
            push = new String[4];
            push[0] = "function=CHANGE_USERNAME";
            push[1] = "client.username=" + client.username;
            push[2] = "client.newUsername=" + username;
            push[3] = END_BREAK;
            client.username = username;
            return push;
        }

        return null;
    }

    /**
     * Used to parse packages
     *
     * @param incoming array of the incoming messages
     * @return a Package
     * @throws WrongPackageFormatException If the incoming package is wrong formated
     */
    public Package parsePackage(String[] incoming) throws WrongPackageFormatException {
        if (!incoming[0].startsWith("function="))
            throw new WrongPackageFormatException(incoming[0], "Function is not specified");
        if (clientType == ClientType.SERVER) {
            return parseClientPackage(incoming);
        } else if (clientType == ClientType.CLIENT) {
            return null;
        }
        return null;
    }

    /**
     * Used by parse package
     *
     * @param clientIncoming array of the incoming
     * @return a Package
     * @throws WrongPackageFormatException If the package is wrong formated
     */
    private Package parseClientPackage(String[] clientIncoming) throws WrongPackageFormatException {
        Package clientPackage = new Package(Function.valueOf(clientIncoming[0].split("=")[1]));
        if (clientPackage == null)
            throw new WrongPackageFormatException(clientIncoming[0], "Function requested not found!");
        if (!clientIncoming[1].startsWith("auth.uuid="))
            throw new WrongPackageFormatException(clientIncoming[1], "No Auth UUID found!");
        clientPackage.authenticateUser(clientIncoming[1].split("=")[1]);
        for (int i = 2; i < clientIncoming.length - 1; i++) {
            if (!clientIncoming[i].contains("=") || clientIncoming[i].split("=").length != 2)
                throw new WrongPackageFormatException(clientIncoming[i], "Arg wrong defined");
            clientPackage.addArg(clientIncoming[i].split("=")[0], clientIncoming[i].split("=")[1]);
        }
        if (clientIncoming[clientIncoming.length - 1] == END_WAIT) {
            clientPackage.setWaitForAnswer(true);
        } else
            clientPackage.setWaitForAnswer(false);
        return clientPackage;
    }


}
