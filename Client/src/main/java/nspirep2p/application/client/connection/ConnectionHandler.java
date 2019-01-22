package nspirep2p.application.client.connection;

import nspirep2p.application.client.Main;
import nspirep2p.application.client.fileHandling.UserPropetySave;
import nspirep2p.communication.protocol.Client;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.v1.CommunicationParser;
import nspirep2p.communication.protocol.v1.Function;
import nspirep2p.communication.protocol.v1.Package;
import nspirep2p.communication.protocol.v1.WrongPackageFormatException;

import java.io.*;
import java.net.Socket;

/**
 * Created by robmroi03 on 12.11.2018.
 * Handles the connection between client and server
 */
public class ConnectionHandler extends Client {

    private int port = 24466;
    private PrintWriter writer;
    private CommunicationParser parser = new CommunicationParser(ClientType.CLIENT);

       public ConnectionHandler() { }

    /**
     * tries to connect to the server
     * @param ip ipAddress of the server
     * @param username username
     * @return if the connection has succeeded
     */
    public boolean connect(String ip, String username){
        UserPropetySave userPropetySave = Main.mainClass.userPropetySave;
        try {
            userPropetySave.generateConfigFile(ip, username);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return connectToServer(getIP(ip), getPort(ip), username);
        //TODO sockets and some fun
    }

    private int getPort(String ip) {
        if (ip.contains(":")){
            port = Integer.parseInt(ip.split(":")[1]);
        }
        return port;
    }

    private String getIP(String ip) {
        if (ip.contains(":")){
            ip =  ip.split(":")[0];
        }
        return ip;
    }

    private boolean connectToServer(String ip, int port, String username) {
        try {
            Socket socket = new Socket(ip, port);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            sendMessage(parser.doHandshake(this));

            sendMessage(parser.pushUsername(this, username));
            this.username = username;

            sendMessage(parser.getChannels(this, null));

            sendMessage(parser.getClients(this, null, false));

            Thread thread = new Thread(new ServerParser(socket));
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    void sendMessage(String[] message) {
        for (String line:message) {
            writer.println(line);
        }
        writer.flush();
    }

    public void createAMessage(String message) {
        sendMessage(Main.mainClass.communicationParser.sendMessage(this, "A channel", message));
    }


    public void changeUsername(String newUsername) {
        String[] usernameChange = parser.pushUsername(this, newUsername);
        sendMessage(usernameChange);
    }

    public void move(String to) {
        String[] move = parser.moveClient(this, to);
        sendMessage(move);
    }


    public void parsePackage(String[] lines) throws WrongPackageFormatException {
        Package parsed = parser.parsePackage(lines);
        switch (parsed.getFunction()) {
            case CHANGE_USERNAME:
                //old == null -> neuer client
                // new == null -> client nicht mehr da
                Main.mainClass.mainInterfaceData.changeUsername(parsed.getArg(Function.CHANGE_USERNAME.getParameters()[0]), parsed.getArg(Function.CHANGE_USERNAME.getParameters()[1]));
                break;
            case MOVE:
                //A client was moved to another Channel. if was in your channel (check this): Say: <br>UserName</br> left
                //if joins your channel: Say <br> Username </br> joined
                //else dont say anything
                break;
            case INVITE:
                //You have been invited.
                //popup -> you have been invited by UserName to join his/her channel ->Join, Not Join
                break;
            case CREATE_TEMP_CHANNEL: //DONE
                Main.mainClass.mainInterfaceData.addChannel(parsed.getArg(Function.CREATE_TEMP_CHANNEL.getParameters()[0]));
                Main.mainClass.mainInterface.reload();
                break;
            case DELETE_TEMP_CHANNEL: //DONE
                Main.mainClass.mainInterfaceData.removeChannel(parsed.getArg(Function.DELETE_TEMP_CHANNEL.getParameters()[0]));
                break;
            case SEND_ERROR:
                //JOptionPane with Error
                break;
            case GET_CLIENTS: //DONE
                String[] userList = parsed.getArg(Function.GET_CLIENTS.getParameters()[0]).split(",");
                for (String user : userList) {
                    Main.mainClass.mainInterfaceData.addUser(user);
                }
                break;
            case GET_CHANNELS:
                String[] channelList = parsed.getArg(Function.GET_CHANNELS.getParameters()[0]).split(",");
                for (String channel : channelList) {
                    Main.mainClass.mainInterfaceData.addChannel(channel);
                }
                break;
            case SEND_MESSAGE: //DONE (without channel)
                Main.mainClass.mainInterface.setNewMessage(parsed.getArg(Function.SEND_MESSAGE.getParameters()[2]),Main.mainClass.getTime(),parsed.getArg(Function.SEND_MESSAGE.getParameters()[1]));
                break;
            default:
                for (String line : lines) {
                    System.out.println(line);
                }
        }
        Main.mainClass.mainInterface.reload();
    }


}
