package nspirep2p.communication.protocol;

import java.util.Random;

/**
 * Handles the protocol between client and server
 * Created by strifel on 07.11.2018.
 */
public class CommunicationParser {
    public static final String PROTOCOL_VERSION = "1.0";
    public static final String END_WAIT = "waiting";
    public static final String END_BREAK = "break";
    private ClientType clientType;

    public CommunicationParser(ClientType clientType){
        this.clientType = clientType;
    }
    /**
     * Handels the handshake beginning for client
     *
     * After Handshake you should get denied or accepted
     * Only accepts clients
     * @return handshake lines
     */
    public String[] doHandshake(Client client){
        String[] handshake;
        if (clientType == ClientType.CLIENT){
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
     * @param handshake the handshake recieved from client
     * @param newClient the client you want to have the informations in
     * @return protocol to send to client
     */
    public String[] parseClientHandshake(String[] handshake, Client newClient){
        if (handshake.length == 3){
            if (handshake[0].contains("=") && handshake[1].contains("=") && !handshake[2].contains("=")){
                if (handshake[0].startsWith("client.nspirep2p.version=") && handshake[1].startsWith("client.nspirep2p.uuid=") && handshake[2].equals("waiting")){
                    if (handshake[0].split("=")[1].equals(PROTOCOL_VERSION)){
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
     *
     * Client: Change your own name
     *         You should get acceptance or deny back
     * Server: Inform clients that a client changed his name
     *         You should not get any response
     * @param client who changes the username
     * @param username the new username
     * @return push commands
     */
    public String[] pushUsername(Client client, String username){
        String[] push;
        if (clientType == ClientType.CLIENT){
            push = new String[3];
            push[0] = "auth.uuid=" + client.uuid;
            push[1] = "client.nspirep2p.username=" + username;
            push[2] = END_WAIT;
            return push;
        }else if (clientType == ClientType.SERVER){
            push = new String[3];
            push[0] = "client.uuid=" + client.uuid;
            push[1] = "client.username=" + username;
            push[2] = END_BREAK;
            client.username = username;
            return push;
        }

        return null;
    }
}
