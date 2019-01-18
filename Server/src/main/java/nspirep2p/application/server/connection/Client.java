package nspirep2p.application.server.connection;

import nspirep2p.application.server.database.Permission;
import nspirep2p.communication.protocol.v1.Package;
import nspirep2p.communication.protocol.v1.*;

import java.io.*;
import java.net.Socket;

public class Client extends nspirep2p.communication.protocol.Client implements Runnable {
    private String role = "user";
    private String channel = "none";
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

    public void send(String[] lines) {
        try {
            for (String s : lines) {
                writer.write(s + "\n");
            }
            writer.flush();
        } catch (IOException e) {
            connectionHandler.main.serverHandler.quit(this);
        }

    }

    public boolean hasPermission(Permission permission){
        return connectionHandler.main.permissionManagment.clientHasPermission(this, permission);
    }

    private void parsePackage(String[] lines) throws WrongPackageFormatException {
        Package parsed = parser.parsePackage(lines);
        Client client = connectionHandler.main.serverHandler.getClientByUUID(parsed.getAuthUUID());
        if (hasPermission(Permission.CONTROL_OTHER) && client != this){
            client = this;
        }else{
            //TODO send Error
        }
        switch (parsed.getFunction()) {
            case CHANGE_USERNAME:
                connectionHandler.main.serverHandler.pushUsernameToClients(client, parsed.getArg(Function.CHANGE_USERNAME.getParameters()[0]));
                break;
            case CREATE_TEMP_CHANNEL:
                connectionHandler.main.serverHandler.createTempChannel(this);
                break;
            case INVITE:
                connectionHandler.main.serverHandler.inviteClient(this, Function.INVITE.getParameters()[0]);
                break;
            case MOVE:
                connectionHandler.main.serverHandler.move(this, parsed.getArg(Function.MOVE.getParameters()[1]));
                break;
        }
    }

    public void run() {
        //Do Handshake
        try {
            multipleLinesReader.clear();
            for (int i = 0; i < 3; i++) {
                String line = reader.readLine();
                if (line.isEmpty()){
                    i--;
                }
                multipleLinesReader.read(line);
            }
            String[] response = parser.parseClientHandshake(multipleLinesReader.getLines(), this);
            send(response);
            if (!response[0].equals("accept")) {
                connectionHandler.main.serverHandler.quit(this);
                return;
            }
            connectionHandler.clients.add(this);
        } catch (IOException e) {
            System.err.println("Exception while handeling client handshake");
            e.printStackTrace();
            connectionHandler.main.serverHandler.quit(this);
        }
        multipleLinesReader.clear();
        System.out.println("New Client connected!");
        //Do normal package parsing
        while (userSocket.isConnected() && connectionHandler.clients.contains(this)) {
            try {
                multipleLinesReader.read(reader.readLine());
                if (multipleLinesReader.isEnd()) {
                    try {
                        parsePackage(multipleLinesReader.getLines());
                    } catch (WrongPackageFormatException e) {
                        send(parser.wrongPackageError());
                    }
                    multipleLinesReader.clear();
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
                connectionHandler.main.serverHandler.quit(this);
            }
        }

        //TODO WHENEVER CLOSE INFORM CLIENTS
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
        connectionHandler.main.serverHandler.quit(this);
    }

    void removeThread() {
        connectionHandler.connections.remove(Thread.currentThread());
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
