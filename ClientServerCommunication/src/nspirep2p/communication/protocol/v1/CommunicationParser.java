package nspirep2p.communication.protocol.v1;

import nspirep2p.communication.protocol.Client;
import nspirep2p.communication.protocol.ClientType;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.Random;

/**
 * Handles the protocol between client and server
 * Created by strifel on 07.11.2018.
 */
public class CommunicationParser {
    private static final String PROTOCOL_VERSION = "1.2";
    static final String END_WAIT = "waiting";
    static final String END_BREAK = "break";
    private ClientType clientType;

    public CommunicationParser(ClientType clientType) {
        this.clientType = clientType;
    }

    /**
     * Handles the handshake beginning for client
     * <p>
     * After Handshake you should get denied or accepted
     * Only accepts clients
     *
     * @return handshake lines
     */
    public String[] doHandshake(Client client) {
        String[] handshake;
        if (clientType == ClientType.CLIENT) {
            if (client.uuid == null) {
                client.uuid = generateNewUUID();
            }
            handshake = new String[3];
            handshake[0] = "client.chatguest.version=" + PROTOCOL_VERSION;
            handshake[1] = "client.chatguest.uuid=" + client.uuid;
            handshake[2] = END_WAIT;
            return handshake;
        }
        return null;
    }

    /**
     * Generates a new uuid
     * @return the uuid as a String
     */
    private String generateNewUUID() {
        long timestamp = new Date().getTime();
        long random = Math.abs(new Random().nextLong());
        long randomFactor = Runtime.getRuntime().totalMemory();
        return "0" + timestamp + "" + (random * (randomFactor / 10000));
    }

    /**
     * Handles parsing of handshake on server side
     * First line of response is either deny or accept
     * This can be used to do Server stuff if denied
     *
     * @param handshake the handshake received from client
     * @param newClient the client you want to have the information in
     * @return protocol to send to client
     */
    public String[] parseClientHandshake(String[] handshake, Client newClient) {
        if (handshake.length == 3) {
            if (handshake[0].contains("=") && handshake[1].contains("=") && !handshake[2].contains("=")) {
                if (handshake[0].startsWith("client.chatguest.version=") && handshake[1].startsWith("client.chatguest.uuid=") && handshake[2].equals("waiting") && !handshake[1].contains(" ")) {
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
     * Sends / replies to pong
     *
     * @param client to get auth uuid
     * @return push to server/client
     */
    public String[] ping(Client client) {
        if (clientType == ClientType.CLIENT) {
            String[] push = new String[3];
            push[0] = "function=PING";
            push[1] = "auth.uuid=" + client.uuid;
            push[2] = END_BREAK;
            return push;
        } else if (clientType == ClientType.SERVER) {
            String[] push = new String[2];
            push[0] = "function=PING";
            push[1] = END_WAIT;
            return push;
        }
        return null;
    }


    public String[] wrongPackageError(){
        return new String[]{"error=package_wrong", END_BREAK};
    }

    /**
     * For both server and client side
     * <p>
     * Client: Change your own name
     * You should get your username change or nothing back
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
            push[2] = "username=" + username;
            push[3] = END_WAIT;
            return push;
        } else if (clientType == ClientType.SERVER) {
            push = new String[4];
            push[0] = "function=CHANGE_USERNAME";
            push[1] = "username=" + client.username;
            push[2] = "newUsername=" + username;
            push[3] = END_BREAK;
            client.username = username;
            return push;
        }

        return null;
    }


    /**
     * For client:
     *   Send request to create a temporary channel
     * For server:
     *   Tell client that there is a new channel
     * @param client the client which want to create/has created the channel
     * @return lines of protocol
     */
    public String[] createTempChannel(Client client) {
        if (clientType == ClientType.CLIENT) {
            String[] push = new String[3];
            push[0] = "function=CREATE_TEMP_CHANNEL";
            push[1] = "auth.uuid=" + client.uuid;
            push[2] = END_WAIT;
            return push;
        } else if (clientType == ClientType.SERVER) {
            String[] push = new String[3];
            push[0] = "function=CREATE_TEMP_CHANNEL";
            push[1] = "username=" + client.username;
            push[2] = END_BREAK;
            return push;
        }
        return null;
    }


    /**
     * Only for server:
     *   Tell client that a temp channel got removed
     * @param client which had own the temp channel
     * @return lines of protocol
     */
    public String[] removeTempChannel(Client client) {
        if (clientType == ClientType.SERVER) {
            String[] push = new String[3];
            push[0] = "function=DELETE_TEMP_CHANNEL";
            push[1] = "username=" + client.username;
            push[2] = END_BREAK;
            return push;
        }
        return null;
    }

    /**
     * For client:
     *   Make the request to get moved to a channel
     * For server:
     *   Tel client that a client got moved
     * @param client the client that moves
     * @param channelName the channel where it moves to
     * @return lines of protocol
     */
    public String[] moveClient(Client client, String channelName) {
        if (clientType == ClientType.CLIENT) {
            String[] push = new String[4];
            push[0] = "function=MOVE";
            push[1] = "auth.uuid=" + client.uuid;
            push[2] = "channel_name=" + channelName;
            push[3] = END_WAIT;
            return push;
        } else if (clientType == ClientType.SERVER) {
            String[] push = new String[4];
            push[0] = "function=MOVE";
            push[1] = "username=" + client.username;
            push[2] = "channel_name=" + channelName;
            push[3] = END_BREAK;
            return push;
        }
        return null;
    }

    /**
     * For client:
     *   Invite a client to your channel
     *
     * For server:
     *   Tell a client that it was invited
     * @param inviter the client that's invite
     * @param recipient the client that's receive (can be null on server side)
     * @return lines of protocol
     */
    public String[] inviteClient(Client inviter, Client recipient) {
        if (clientType == ClientType.CLIENT) {
            String[] push = new String[4];
            push[0] = "function=INVITE";
            push[1] = "auth.uuid=" + inviter.uuid;
            push[2] = "username=" + recipient.username;
            push[3] = END_BREAK;
            return push;
        } else if (clientType == ClientType.SERVER) {
            String[] push = new String[3];
            push[0] = "function=INVITE";
            push[1] = "username=" + inviter.username;
            push[2] = END_BREAK;
            return push;
        }
        return null;
    }

    /**
     * For a client:
     * Send Message to Server
     *
     * For Server:
     * Send recieved Message to another client
     */
    public String[] sendMessage(Client sender, String channel, String message) {
        if (clientType == ClientType.CLIENT) {
            String[] push = new String[5];
            push[0] = "function=SEND_MESSAGE";
            push[1] = "auth.uuid=" + sender.uuid;
            push[2] = "channel=" + channel;
            push[3] = "message=" + message;
            push[4] = END_BREAK;
            return push;
        } else if (clientType == ClientType.SERVER) {
            String[] push = new String[5];
            push[0] = "function=SEND_MESSAGE";
            push[1] = "channel=" + channel;
            push[2] = "message=" + message;
            push[3] = "username=" + sender.username;
            push[4] = END_BREAK;
            return push;
        }
        return null;
    }

    /**
     * For client:
     * Try to kick user
     * <p>
     * For server:
     * Inform client that he got kicked
     *
     * @param toBeKicked the client which should be kicked (not needed on serverside)
     * @param client     the client who wants to kick the user (not needed on serverside)
     * @param reason     the reason why the client should be kicked
     */
    public String[] kick(Client client, Client toBeKicked, String reason) {
        if (clientType == ClientType.CLIENT) {
            String[] push = new String[5];
            push[0] = "function=KICK";
            push[1] = "auth.uuid=" + client.uuid;
            push[2] = "username=" + toBeKicked.username;
            push[3] = "reason=" + reason;
            push[4] = END_BREAK;
            return push;
        } else if (clientType == ClientType.SERVER) {
            String[] push = new String[3];
            push[0] = "function=KICK";
            push[1] = "reason=" + reason;
            push[2] = END_BREAK;
            return push;
        }
        return null;
    }


    /**
     *
     * For client:
     * Send an request to server to get Channels Listed
     * Gives you a comma separated list with channels
     *
     * For Server:
     * Send all Channels to client
     * @param client the client which requested
     * @param channels all channels (null on client)
     * @return push
     */
    public String[] getChannels(Client client,String[] channels){
        if(clientType == ClientType.SERVER){
            String[] push = new String[3];
            push[0] = "function=GET_CHANNELS";
            push[1] = Function.GET_CHANNELS.getParameters()[0] + "=";
            for (String s:channels) {
                push[1] += s+",";
            }
            push[1] = push[1].substring(0, push[1].length() -1);
            push[2] = END_BREAK;
            return push;
        }
        else if(clientType == ClientType.CLIENT){
            String[] push = new String[3];
            push[0] = "function=GET_CHANNELS";
            push[1] = "auth.uuid=" + client.uuid;
            push[2] = END_WAIT;
            return push;

        }
        return null;

    }


    /**
     * For client:
     * Send Request to server to list clients.
     * For Server:
     * Returns list with all clients to client
     *
     * @param client   the client which requested
     * @param clients all clients (null on client)
     * @param sendUUID if uuid's should be send too (false on client)
     * @return push
     */
    public String[] getClients(Client client, Client[] clients, boolean sendUUID) {
        if(clientType == ClientType.SERVER){
            String[] push = new String[4];
            push[0] = "function=GET_CLIENTS";
            push[1] = Function.GET_CLIENTS.getParameters()[0] + "=";
            push[2] = Function.GET_CLIENTS.getParameters()[1] + "=";
            for (Client s:clients) {
                push[1] += s.username+",";
                if (sendUUID){
                    push[2] += s.uuid + ",";
                }else{
                    push[2] += new Random() + ",";
                }    
            }
            push[1] = push[1].substring(0, push[1].length() -1);
            push[2] = push[2].substring(0, push[2].length() -1);
            push[3] = END_BREAK;
            return push;
        } else if (clientType == ClientType.CLIENT) {
            String[] push = new String[3];
            push[0] = "function=GET_CLIENTS";
            push[1] = "auth.uuid=" + client.uuid;
            push[2] = END_WAIT;
            return push;

        }
        return null;

    }

    /**
     * Only from server to client:
     * Send an error
     *
     * @param error the error which should be sent (could be any string (e.g. Shutdown now or You do not have the right to do that)
     * @return push
     */
    public String[] sendError(String error) {
        if (clientType == ClientType.SERVER) {
            String[] push = new String[3];
            push[0] = "function=SEND_ERROR";
            push[1] = Function.SEND_ERROR.getParameters()[0] + "=" + error;
            push[2] = END_BREAK;
            return push;
        }
        return new String[]{};
    }
    /**
     * Enter a group with a password
     *
     * @param client            the client which wants to enter
     * @param cleartextPassword the password
     * @return
     */
    public String[] enterGroup(Client client, String cleartextPassword) {
        if (clientType == ClientType.CLIENT) {
            try {
                String hashed = Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(cleartextPassword.getBytes()));
                String[] push = new String[4];
                push[0] = "function=ENTER_GROUP";
                push[1] = "auth.uuid=" + client.uuid;
                push[2] = Function.ENTER_GROUP.getParameters()[0] + "=" + hashed;
                push[3] = END_BREAK;
                return push;

            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return new String[]{};
    }

    /**
     * Used to parse packages
     *
     * @param incoming array of the incoming messages
     * @return a Package
     * @throws WrongPackageFormatException If the incoming package is wrong formatted
     */
    public Package parsePackage(String[] incoming) throws WrongPackageFormatException {
        if (!incoming[0].startsWith("function="))
            throw new WrongPackageFormatException(incoming[0], "Function is not specified");
        if (clientType == ClientType.SERVER) {
            return parseClientPackage(incoming);
        } else if (clientType == ClientType.CLIENT) {
            return parseServerPackage(incoming);
        }
        return null;
    }

    /**
     * Used by parse package
     *
     * @param clientIncoming array of the incoming
     * @return a Package
     * @throws WrongPackageFormatException If the package is wrong formatted
     */
    @SuppressWarnings("Duplicates")
    private Package parseClientPackage(String[] clientIncoming) throws WrongPackageFormatException {
        Package clientPackage = new Package(Function.valueOf(clientIncoming[0].split("=")[1]));
        if (clientPackage == null)
            throw new WrongPackageFormatException(clientIncoming[0], "Function requested not found!");
        if (!clientIncoming[1].startsWith("auth.uuid="))
            throw new WrongPackageFormatException(clientIncoming[1], "No Auth UUID found!");
        clientPackage.authenticateUser(clientIncoming[1].split("=")[1]);
        if(clientIncoming.length>2) {
            for (int i = 2; i < clientIncoming.length - 1; i++) {
                if (!clientIncoming[i].contains("="))
                    throw new WrongPackageFormatException(clientIncoming[i], "Arg wrong defined");
                clientPackage.addArg(clientIncoming[i].split("=", 2)[0], clientIncoming[i].split("=", 2)[1]);
            }
        }
        if (clientIncoming[clientIncoming.length - 1] == END_WAIT) {
            clientPackage.setWaitForAnswer(true);
        } else
            clientPackage.setWaitForAnswer(false);
        return clientPackage;
    }

    /**
     * Used by parse package
     *
     * @param clientIncoming array of the incoming
     * @return a Package
     * @throws WrongPackageFormatException If the package is wrong formatted
     */
    @SuppressWarnings("Duplicates")
    private Package parseServerPackage(String[] clientIncoming) throws WrongPackageFormatException {
        Package clientPackage = new Package(Function.valueOf(clientIncoming[0].split("=")[1]));
        if (clientPackage == null)
            throw new WrongPackageFormatException(clientIncoming[0], "Function requested not found!");
        if(clientIncoming.length > 2) {
            for (int i = 1; i < clientIncoming.length - 1; i++) {
                if (!clientIncoming[i].contains("="))
                    throw new WrongPackageFormatException(clientIncoming[i], "Arg wrong defined");
                clientPackage.addArg(clientIncoming[i].split("=", 2)[0], clientIncoming[i].split("=", 2)[1]);
            }
        }
        if (clientIncoming[clientIncoming.length - 1] == END_WAIT) {
            clientPackage.setWaitForAnswer(true);
        } else
            clientPackage.setWaitForAnswer(false);
        return clientPackage;
    }



}
