package nspirep2p.application.server;

import nspirep2p.application.server.connection.Client;
import nspirep2p.application.server.connection.ConnectionHandler;
import nspirep2p.application.server.database.Permission;

import java.util.ArrayList;
import java.util.Arrays;
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
                forceMove(client, client.username);
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
     * @param otherClient the recipient
     */
    public void inviteClient(Client client, String otherClient){
        Client client2 = getClientByUsername(otherClient);
        if (client2 == null) return;
        allowedClients.get(client).add(client2);
        client2.send(connectionHandler.parser.inviteClient(client, client2));

    }


    /**
     * Send Message from Client
     *
     * @param client  the client who send it
     * @param message the message which will be send
     */
    public void sendMessage(Client client, String message) {
        for (Client recipient : getClientsInChannel(client.getChannel())) {
            recipient.send(connectionHandler.parser.sendMessage(client, client.getChannel(), message));
        }
    }

    /**
     * Send an error message to a client
     *
     * @param client  the client which should receive the error
     * @param message the message which should be send
     */
    public void sendErrorMessage(Client client, String message) {
        client.send(connectionHandler.parser.sendError(message));
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

    /**
     * Get all clients which are in a channel
     * @param channel which you want to check
     * @return the clients as array
     */
    private Client[] getClientsInChannel(String channel) {
        ArrayList<Client> clients = new ArrayList<>();
        for (Client client : connectionHandler.getClients()){
            if (client.getChannel().equals(channel)){
                clients.add(client);
            }
        }
        return Arrays.copyOf(clients.toArray(), clients.size(), Client[].class);
    }

    /**
     * Move the client without checking anything
     * @param client the client which should be used
     * @param channel the channel where moved to
     */
    private void forceMove(Client client, String channel) {
        connectionHandler.broadcast(connectionHandler.parser.moveClient(client, channel));
        client.setChannel(channel);
    }

    /**
     * Check if a user owns a private channel
     * @param client the client which should be checked
     * @return if the private channel exists
     */
    private boolean hasPrivateChannel(Client client) {
        return privateChannels.contains(client);
    }

    /**
     * Check if a user owns a private channel
     * @param name the client name which should be checked
     * @return if the private channel exists
     */
    private boolean doesPrivateChannelExists(String name) {
        return hasPrivateChannel(getClientByUsername(name));
    }

    /**
     * Move a client (check if possible before)
     * @param client the client
     * @param channel the channel
     */
    public void move(Client client, String channel){
        //Remove channel when owner quits
        if (privateChannels.contains(client)) {
            deletePrivateChannel(client);
        }
        //Move client
        //TODO: public channels
        if (doesPrivateChannelExists(channel) && (allowedClients.get(getClientByUsername(channel)).contains(client) || client.hasPermission(Permission.JOIN_ANY))) {
            forceMove(client, channel);
        }
    }

    /**
     * Delete a private channel
     *
     * @param owner of the channel
     */
    private void deletePrivateChannel(Client owner) {
        if (privateChannels.contains(owner)) {
            for (Client client : getClientsInChannel(owner.username)) {
                client.send(connectionHandler.parser.moveClient(client, "none"));
            }
            privateChannels.remove(owner);
            connectionHandler.broadcast(connectionHandler.parser.removeTempChannel(owner));
        }
    }

    /**
     * Quit a client
     *
     * @param client which quits
     */
    public void quit(Client client) {
        connectionHandler.clients.remove(client);
        connectionHandler.deleteThread(client);
        deletePrivateChannel(client);
        connectionHandler.broadcast(connectionHandler.parser.pushUsername(client, "null"));
    }

    /**
     * Enters the group a user entered a key for
     * If key is wrong it enters standard user group
     *
     * @param client which will enter
     * @param hashed key
     */
    public void enterGroup(Client client, String hashed) {
        client.setRole(main.permissionManagment.checkKey(hashed));
    }

    /**
     * Send all clients to a client
     *
     * @param client which should get the information
     */
    public void sendClientsToClient(Client client) {
        client.send(connectionHandler.parser.getClients(
                client,
                connectionHandler.clients.toArray(new nspirep2p.communication.protocol.Client[0]),
                main.permissionManagment.clientHasPermission(client, Permission.READ_UUID)));
    }

    /**
     * Sends all channels to client
     *
     * @param client who should get the channel list
     */
    public void sendChannelsToClient(Client client) {
        ArrayList<String> channels = new ArrayList<>();
        //TODO add public channels
        for (Client channelOwner : privateChannels) {
            channels.add(channelOwner.username);
        }
        client.send(connectionHandler.parser.getChannels(
                client,
                channels.toArray(new String[0])
        ));
    }
}
