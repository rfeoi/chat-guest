package nspirep2p.application.server.connection;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class AcceptRunnable implements Runnable {
    private ServerSocket serverSocket;
    private ConnectionHandler connectionHandler;

    AcceptRunnable(ServerSocket serverSocket, ConnectionHandler connectionHandler) {
        this.serverSocket = serverSocket;
        this.connectionHandler = connectionHandler;
    }


    public void run() {
        while (connectionHandler.serverRun) {
            if (connectionHandler.connections.size() < connectionHandler.maxUser) {
                try {
                    Socket socket = serverSocket.accept();
                    Thread userThread = new Thread(new Client(socket, connectionHandler));
                    userThread.start();
                    connectionHandler.connections.add(userThread);
                } catch (SocketException e) {
                    System.out.println("Server closed!");
                } catch (IOException e) {
                    System.err.println("Error happend while tried to accept a new client");
                    e.printStackTrace();
                }
            }
        }
    }
}
