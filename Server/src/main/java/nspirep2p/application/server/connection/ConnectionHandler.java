package nspirep2p.application.server.connection;

import nspirep2p.application.server.Main;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.CommunicationParser;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

/**
 * Handels the Connections serverside
 * Created by strifel on 07.11.2018.
 */
public class ConnectionHandler {
    private ServerSocket serverSocket;
    Main main;
    ArrayList<Thread> connections;
    ArrayList<Client> clients;
    public CommunicationParser parser;
    int maxUser;
    private Thread acceptThread;
    boolean serverRun = false;

    public ConnectionHandler(Main main, int port, int maxUser) throws IOException {
        this.main = main;
        serverSocket = new ServerSocket(port);
        clients = new ArrayList<Client>(port);
        connections = new ArrayList<Thread>(maxUser);
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
    public void stop() {
        //TODO tell user that server stopped
        acceptThread.stop();
        serverRun = false;
    }


    /**
     * Broadcast a line to all clients
     *
     * @param lines which should be printed
     */
    public void broadcast(String[] lines) {
        for (Client client : clients) {
            try {
                client.send(lines);
            } catch (IOException e) {
                System.err.println("Could not reach client " + client.username + " with UUID " + client.uuid + " on " + client.userSocket.getInetAddress().getHostAddress());
                e.printStackTrace();
            }
        }
    }

    public ArrayList<Client> getClients() {
        return clients;
    }
}
