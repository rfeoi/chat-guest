package nspirep2p.application.client.connection;

import nspirep2p.application.client.Main;
import nspirep2p.application.client.fileHandling.UserPropetySave;
import nspirep2p.communication.protocol.Client;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.v1.CommunicationParser;
import nspirep2p.communication.protocol.v1.Function;
import nspirep2p.communication.protocol.v1.Package;
import nspirep2p.communication.protocol.v1.WrongPackageFormatException;

import javax.swing.*;
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

    private void sendMessage(String[] message) {
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
                String oldUsername = parsed.getArg(Function.CHANGE_USERNAME.getParameters()[0]);
                String newUsername = parsed.getArg(Function.CHANGE_USERNAME.getParameters()[1]);
                if (oldUsername == null) {
                    Main.mainClass.mainInterface.setNewServerMessage("<br>" + newUsername + "</br> joined");
                    Main.mainClass.mainInterfaceData.addUser(newUsername);
                } else if (newUsername == null) {
                    Main.mainClass.mainInterface.setNewServerMessage("<br>" + oldUsername + "</br> left");
                    Main.mainClass.mainInterfaceData.removeUser(oldUsername);
                } else {
                    Main.mainClass.mainInterfaceData.changeUsername(parsed.getArg(Function.CHANGE_USERNAME.getParameters()[0]), parsed.getArg(Function.CHANGE_USERNAME.getParameters()[1]));
                }
                break;
            case MOVE:
                String username = parsed.getArg(Function.MOVE.getParameters()[0]);
                String changeChannel = parsed.getArg(Function.MOVE.getParameters()[1]);
                if (changeChannel.equals("another Channel")/* && username.wasInYourChannel*/) {
                    Main.mainClass.mainInterface.setNewServerMessage("<br>" + username + "</br> left");
                    Main.mainClass.mainInterfaceData.removeUser(username);
                } else if (changeChannel.equals(Main.mainClass.mainInterfaceData.getCurrentChannel())) {
                    Main.mainClass.mainInterface.setNewServerMessage("<br>" + username + "</br> joined");
                    Main.mainClass.mainInterfaceData.addUser(username);
                }
                break;
            case INVITE:
                int joinInt = JOptionPane.showConfirmDialog(null, "You have been invited by " +
                        parsed.getArg(Function.INVITE.getParameters()[0]) + " to join their channel. \n" +
                        "Do you want to join?", "You have been invited", JOptionPane.YES_NO_OPTION);
                if (joinInt == JOptionPane.YES_OPTION) {
                    move(parsed.getArg(Function.INVITE.getParameters()[0]));
                }
                break;
            case CREATE_TEMP_CHANNEL: //DONE
                Main.mainClass.mainInterfaceData.addChannel(parsed.getArg(Function.CREATE_TEMP_CHANNEL.getParameters()[0]));
                Main.mainClass.mainInterface.reload();
                break;
            case DELETE_TEMP_CHANNEL: //DONE
                Main.mainClass.mainInterfaceData.removeChannel(parsed.getArg(Function.DELETE_TEMP_CHANNEL.getParameters()[0]));
                break;
            case SEND_ERROR:
                JOptionPane.showMessageDialog(null, parsed.getArg(Function.SEND_ERROR.getParameters()[0]));
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
