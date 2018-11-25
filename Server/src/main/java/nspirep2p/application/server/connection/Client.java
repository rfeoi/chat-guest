package nspirep2p.application.server.connection;

import nspirep2p.communication.protocol.v1.*;
import nspirep2p.communication.protocol.v1.Package;

import java.io.*;
import java.net.Socket;

public class Client extends nspirep2p.communication.protocol.Client implements Runnable {
    Socket userSocket;
    private ConnectionHandler connectionHandler;
    private CommunicationParser parser;
    private MultipleLinesReader multipleLinesReader;
    private BufferedReader reader;
    private BufferedWriter writer;

    Client(Socket socket, ConnectionHandler connectionHandler) throws IOException {
        this.connectionHandler = connectionHandler;
        userSocket = socket;
        reader = new BufferedReader(new InputStreamReader(userSocket.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(userSocket.getOutputStream()));
        multipleLinesReader = new MultipleLinesReader();
        parser = connectionHandler.parser;
    }

    void send(String[] lines) throws IOException {
        for (String s : lines) {
            writer.write(s + "\n");
        }
    }

    private void parsePacket(String[] lines) throws WrongPackageFormatException {
        Package parsed = parser.parsePackage(lines);
        Client client = connectionHandler.main.serverHandler.getClientByUUID(parsed.getAuthUUID());
        if (client != this ){
            //TODO only set to this if no permission to use other user
            client = this;
        }
        switch (parsed.getFunction()) {
            case CHANGE_USERNAME:
                connectionHandler.main.serverHandler.pushUsernameToClients(client, parsed.getArg(Function.CHANGE_USERNAME.getParameters()[0]));
                break;
        }
    }

    public void run() {
        //Do Handshake
        try {
            multipleLinesReader.clear();
            for (int i = 0; i < 3; i++) {
                multipleLinesReader.read(reader.readLine());
            }
            String[] response = parser.parseClientHandshake(multipleLinesReader.getLines(), this);
            send(response);
            if (!response[0].equals("accept")) {
                connectionHandler.connections.remove(Thread.currentThread());
                return;
            }
            connectionHandler.clients.add(this);
        } catch (IOException e) {
            System.err.println("Exception while handeling client handshake");
            e.printStackTrace();
        }
        multipleLinesReader.clear();

        //Do normal package parsing
        while (userSocket.isConnected() && connectionHandler.clients.contains(this)) {
            try {
                multipleLinesReader.read(reader.readLine());
                if (multipleLinesReader.isEnd()) {
                    try {
                        parsePacket(multipleLinesReader.getLines());
                    } catch (WrongPackageFormatException e) {
                        send(parser.wrongPackageError());
                    }
                    multipleLinesReader.clear();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Do close socket
        if (userSocket.isConnected()) {
            try {
                writer.close();
                reader.close();
                userSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (connectionHandler.clients.contains(this)) connectionHandler.clients.remove(this);
        connectionHandler.connections.remove(Thread.currentThread());
    }
}
