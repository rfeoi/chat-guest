package nspirep2p.application.server;

import nspirep2p.application.server.connection.Client;
import nspirep2p.application.server.connection.ConnectionHandler;

public class ServerHandler {

    private Main main;
    private ConnectionHandler connectionHandler;

    ServerHandler(Main main) {
        this.main = main;
        connectionHandler = main.connectionHandler;
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
     * Searches a client by his username
     *
     * @param username The username of the client (case sensitive)
     * @return a client or null if none found
     */
    private Client getClientByUsername(String username) {
        for (Client client : connectionHandler.getClients()) {
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
