package nspirep2p.application.server.connection;

import nspirep2p.application.server.Main;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.v1.CommunicationParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;

/**
 * Handels the Connections serverside
 * Created by strifel on 07.11.2018.
 */
public class ConnectionHandler {
    private ServerSocket serverSocket;
    Main main;
    ArrayList<Thread> connections;
    public ArrayList<Client> clients;
    public CommunicationParser parser;
    int maxUser;
    private Thread acceptThread;
    boolean serverRun = false;

    public ConnectionHandler(Main main, int port, int maxUser) throws IOException {
        this.main = main;
        serverSocket = new ServerSocket(port);
        clients = new ArrayList<>(port);
        connections = new ArrayList<>(maxUser);
        parser = new CommunicationParser(ClientType.SERVER);
        this.maxUser = maxUser;

    }

    /**
     * Starts server
     */
    public void start() {
        serverRun = true;
        acceptThread = new Thread(new AcceptRunnable(serverSocket, this));
        acceptThread.start();
    }

    /**
     * Stops server
     */
    @SuppressWarnings("unused")
    public void stop() throws IOException {
        //TODO tell user that server stopped
        serverRun = false;
        serverSocket.close();
    }

    /**
     * Delete and stop a thread
     *
     * @param client which should be stopped
     */
    public void deleteThread(Client client) {
        client.removeThread();
    }


    /**
     * Broadcast a line to all clients
     *
     * @param lines which should be printed
     */
    @SuppressWarnings("CatchMayIgnoreException")
    public void broadcast(String[] lines) {
        try {
            for (Client client : clients) {
                client.send(lines);
            }
        } catch (ConcurrentModificationException e) {
        }
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
}
