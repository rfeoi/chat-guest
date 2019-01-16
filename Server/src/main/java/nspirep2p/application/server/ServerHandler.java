package nspirep2p.application.server;

import nspirep2p.application.server.connection.Client;
import nspirep2p.application.server.connection.ConnectionHandler;
import nspirep2p.application.server.database.Permission;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ServerHandler {

    private Main main;
    private ConnectionHandler connectionHandler;
    private ArrayList<Client> privateChannels;
    private HashMap<Client, ArrayList<Client>> allowedClients;

    ServerHandler(Main main) {
        this.main = main;
        connectionHandler = main.connectionHandler;
        privateChannels = new ArrayList<>();
        allowedClients = new HashMap<>();
    }

    /**
     * Tells all clients about a new username
     *
     * @param client      the client who changes his username
     * @param newUsername the new username
     */
    public void pushUsernameToClients(Client client, String newUsername) {
        if (getClientByUsername(newUsername) == null) {
            connectionHandler.broadcast(connectionHandler.parser.pushUsername(client, newUsername));
        }
    }

    /**
     * When user tries to create a temp channel
     *
     * @param client the client which tries to create a temp channel
     */
    public void createTempChannel(Client client) {
        if (client.hasPermission(Permission.CREATE_TEMP_CHANNEL)) {
            if (!privateChannels.contains(client)) {
                privateChannels.add(client);
                connectionHandler.broadcast(connectionHandler.parser.createTempChannel(client));
                allowedClients.put(client, new ArrayList<Client>());
                allowedClients.get(client).add(client);
            } else {
                sendErrorMessage(client, "You already have a Channel. Join them instead.");
            }
        } else {
            sendErrorMessage(client, Permission.CREATE_TEMP_CHANNEL.getNoPermissionError());
        }
    }

    /**
     * Invite a client to your channel
     * Technically this adds the permission to join this channel and then sends the invitation prompt
     * @param client the inviter or (client owner)
     * @param otherClient
     */
    public void inviteClient(Client client, String otherClient){
        Client client2 = getClientByUsername(otherClient);
        if (client2 == null) return;
        allowedClients.get(client).add(client2);
        try {
            client2.send(connectionHandler.parser.inviteClient(client, client2));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendErrorMessage(Client client, String message) {
        //TODO
    }

    /**
     * Searches a client by his username
     *
     * @param username The username of the client (case sensitive)
     * @return a client or null if none found
     */
    private Client getClientByUsername(String username) {
        for (Client client : connectionHandler.getClients()) {
            if (client.username == null) continue;
            if (client.username.equals(username)) return client;
        }
        return null;
    }

    /**
     * Searches a client by his uuid
     *
     * @param uuid The uuid of the client
     * @return a client or null if none found
     */
    public Client getClientByUUID(String uuid) {
        for (Client client : connectionHandler.getClients()) {
            if (client.uuid.equals(uuid)) return client;
        }
        return null;
    }

}
