package nspirep2p.application.server.connection;

import nspirep2p.application.server.database.Permission;
import nspirep2p.communication.protocol.v1.Package;
import nspirep2p.communication.protocol.v1.*;

import java.io.*;
import java.net.Socket;
import java.util.Date;

public class Client extends nspirep2p.communication.protocol.Client implements Runnable {
    private String role = "user";
    private String channel = "none";
    private Socket userSocket;
    private long lastPing;
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
        if (client != this && !hasPermission(Permission.CONTROL_OTHER)) {
            client = this;
            connectionHandler.main.serverHandler.sendErrorMessage(this, Permission.CONTROL_OTHER.getNoPermissionError());
        }
        switch (parsed.getFunction()) {
            case CHANGE_USERNAME:
                connectionHandler.main.serverHandler.pushUsernameToClients(client, parsed.getArg(Function.CHANGE_USERNAME.getParameters()[0]));
                break;
            case CREATE_TEMP_CHANNEL:
                connectionHandler.main.serverHandler.createTempChannel(client);
                break;
            case INVITE:
                connectionHandler.main.serverHandler.inviteClient(client, parsed.getArg(Function.INVITE.getParameters()[0]));
                break;
            case MOVE:
                connectionHandler.main.serverHandler.move(client, parsed.getArg(Function.MOVE.getParameters()[1]));
                break;
            case SEND_MESSAGE:
                connectionHandler.main.serverHandler.sendMessage(client, parsed.getArg(Function.SEND_MESSAGE.getParameters()[1]));
                break;
            case ENTER_GROUP:
                connectionHandler.main.serverHandler.enterGroup(client, parsed.getArg(Function.ENTER_GROUP.getParameters()[0]));
                break;
            case GET_CLIENTS:
                connectionHandler.main.serverHandler.sendClientsToClient(this);
                break;
            case GET_CHANNELS:
                connectionHandler.main.serverHandler.sendChannelsToClient(this);
                break;
            case KICK:
                connectionHandler.main.serverHandler.kickClient(client, parsed.getArg(Function.KICK.getParameters()[0]), parsed.getArg(Function.KICK.getParameters()[1]));
                break;
            default:
                connectionHandler.main.serverHandler.sendErrorMessage(this, "Something went wrong! Server does not implements " + parsed.getFunction());
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
            if (connectionHandler.main.serverHandler.getClientByUUID(uuid) == null) {
                connectionHandler.clients.add(this);
            } else {
                connectionHandler.main.serverHandler.sendMessage(this, "Sorry UUID is already taken. Please reconnect with an other uuid!");
                return;
            }
        } catch (IOException e) {
            System.err.println("Exception while handeling client handshake");
            e.printStackTrace();
            connectionHandler.main.serverHandler.quit(this);
        }
        multipleLinesReader.clear();
        System.out.println("New Client connected with UUID " + uuid + "! (From " + userSocket.getInetAddress().getHostAddress() + ")");
        //Do normal package parsing
        while (userSocket.isConnected() && connectionHandler.clients.contains(this)) {
            try {
                multipleLinesReader.read(reader.readLine());
                if (multipleLinesReader.isEnd()) {
                    try {
                        lastPing = new Date().getTime();
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
            if (new Date().getTime() - lastPing > 15000) {
                connectionHandler.main.serverHandler.quit(this);
            } else if (new Date().getTime() - lastPing > 10000) {
                send(connectionHandler.parser.ping(this));
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
