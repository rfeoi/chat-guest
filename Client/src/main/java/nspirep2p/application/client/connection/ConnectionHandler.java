package nspirep2p.application.client.connection;

import nspirep2p.application.client.Main;
import nspirep2p.communication.protocol.Client;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.v1.CommunicationParser;
import nspirep2p.communication.protocol.v1.Function;
import nspirep2p.communication.protocol.v1.Package;
import nspirep2p.communication.protocol.v1.WrongPackageFormatException;

import javax.swing.*;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;

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
    public boolean connect(String ip, String username, String uuid){
        try {
            Main.mainClass.userPropetySave.generateConfigFile(ip, username, uuid);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return connectToServer(getIP(ip), username, uuid, getPort(ip));
    }

    /**
     * Splits IP-address and port
     * @param ip ipAddress and port in a String
     * @return the port
     */
    private int getPort(String ip) {
        if (ip.contains(":")){
            port = Integer.parseInt(ip.split(":")[1]);
        }
        return port;
    }

    /**
     * Splits IP-address and port
     * @param ip ipAddress and port in a String
     * @return the ip
     */
    private String getIP(String ip) {
        if (ip.contains(":")){
            ip =  ip.split(":")[0];
        }
        return ip;
    }

    /**
     * Starts the connection to the server and sends the starting messages/functions.
     * @param ip ipAddress of the server
     * @param port port of the server
     * @param username username of the user
     * @return if the connection has succeeded
     */
    private boolean connectToServer(String ip, String username, String uuid, int port) {
        try {
            Socket socket = new Socket(ip, port);
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));
            this.username = username;
            this.uuid = uuid;
            sendMessage(parser.doHandshake(this));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Main.mainClass.userPropetySave.generateConfigFile("" + ip + ":" + port, this.username, this.uuid);


            sendMessage(parser.pushUsername(this, username));

            sendMessage(parser.getChannels(this, null));

            sendMessage(parser.getClients(this, null, false));

            Thread thread = new Thread(new ServerParser(socket));
            thread.start();
        } catch (ConnectException e) {
            //e.printStackTrace();
            System.out.println("ConnectException");
            return false;
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException");
            //e.printStackTrace();
            return false;
        } catch (IOException e) {
            System.out.println("IOException");
            //e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Sends a multiple line message to the server.
     * @param message the messages in an array.
     */
    private void sendMessage(String[] message) {
        for (String line:message) {
            writer.println(line);
        }
        writer.flush();
    }

    /**
     * Creates a message
     * @param message the content of the message
     */
    public void createAMessage(String message) {
        sendMessage(Main.mainClass.communicationParser.sendMessage(this, null, message));
    }

    /**
     * Changes the username
     * @param newUsername the new username
     */
    public void changeUsername(String newUsername) {
        sendMessage(parser.pushUsername(this, newUsername));
    }

    /**
     * Moves the client to another channel.
     * @param to the other channel
     */
    public void move(String to) {
        Main.mainClass.mainInterface.setNewServerMessage("You moved to: " + to);
        sendMessage(parser.moveClient(this, to));
    }

    /**
     * Invites another user and creates a channel if necessary.
     * @param user the user that will be invited
     * @param createTempChannel whether (not) a temporary channel is necessary.
     */
    public void invite(String user, boolean createTempChannel) {
        if (createTempChannel) {
            sendMessage(parser.createTempChannel(this));
        }
        Client client = new Client();
        client.username = user;
        sendMessage(parser.inviteClient(this, client));
    }

    /**
     * Enters the client in a group.
     * @param key the password
     */
    public void setGroup(String key) {
        sendMessage(parser.enterGroup(this, key));
    }

    /**
     * Sends a message to the server to kick a specific user
     * @param user the user that should be kicked
     * @param reason the reason why the user should be kicked
     */
    public void kickUser(String user, String reason) {
        Client client = new Client();
        client.username = user;
        sendMessage(parser.kick(this, client, reason));
    }

    /**
     * Parses the package which the client receives from the server.
     * @param lines the package in an array.
     * @throws WrongPackageFormatException if the package does not conform to the typical package.
     */
    public void parsePackage(String[] lines) throws WrongPackageFormatException {
        Package parsed = parser.parsePackage(lines);
        switch (parsed.getFunction()) {
            case CHANGE_USERNAME:
                String oldUsername = parsed.getArg(Function.CHANGE_USERNAME.getParameters()[0]);
                String newUsername = parsed.getArg(Function.CHANGE_USERNAME.getParameters()[1]);
                if (oldUsername.equals("null") && !newUsername.equals("null")) {
                    Main.mainClass.mainInterface.setNewServerMessage("<b>" + newUsername + "</b> joined");
                    Main.mainClass.mainInterfaceData.addUser(newUsername);
                } else if (newUsername.equals("null")) {
                    Main.mainClass.mainInterface.setNewServerMessage("<b>" + oldUsername + "</b> left");
                    Main.mainClass.mainInterfaceData.removeUser(oldUsername);
                } else {
                    Main.mainClass.mainInterfaceData.changeUsername(oldUsername, newUsername);
                    Main.mainClass.mainInterfaceData.changeChannelName(oldUsername, newUsername);
                }
                break;
            case MOVE:
                String username = parsed.getArg(Function.MOVE.getParameters()[0]);
                String changeChannel = parsed.getArg(Function.MOVE.getParameters()[1]);
                if (!changeChannel.equals(Main.mainClass.mainInterfaceData.getCurrentChannel()) && Main.mainClass.mainInterfaceData.userIsInYourChannel(username)) {
                    Main.mainClass.mainInterface.setNewServerMessage("<b>" + username + "</b> left");
                    //Main.mainClass.mainInterfaceData.removeUser(username);
                } else if (changeChannel.equals(Main.mainClass.mainInterfaceData.getCurrentChannel())) {
                    if (username.equals("null")) return;
                    Main.mainClass.mainInterface.setNewServerMessage("<b>" + username + "</b> joined");
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
            case CREATE_TEMP_CHANNEL:
                Main.mainClass.mainInterfaceData.addChannel(parsed.getArg(Function.CREATE_TEMP_CHANNEL.getParameters()[0]));
                Main.mainClass.mainInterface.reload();
                break;
            case DELETE_TEMP_CHANNEL:
                Main.mainClass.mainInterfaceData.removeChannel(parsed.getArg(Function.DELETE_TEMP_CHANNEL.getParameters()[0]));
                break;
            case SEND_ERROR:
                JOptionPane.showMessageDialog(null, parsed.getArg(Function.SEND_ERROR.getParameters()[0]));
                break;
            case GET_CLIENTS:
                String[] userList = parsed.getArg(Function.GET_CLIENTS.getParameters()[0]).split(",");
                Main.mainClass.mainInterfaceData.removeAllUsers();
                for (String user : userList) {
                    Main.mainClass.mainInterfaceData.addUser(user);
                }
                break;
            case GET_CHANNELS:
                String[] channelList = parsed.getArg(Function.GET_CHANNELS.getParameters()[0]).split(",");
                Main.mainClass.mainInterfaceData.removeAllChannel();
                for (String channel : channelList) {
                    Main.mainClass.mainInterfaceData.addChannel(channel);
                }
                break;
            case SEND_MESSAGE:
                Main.mainClass.mainInterface.setNewMessage(parsed.getArg(Function.SEND_MESSAGE.getParameters()[2]),Main.mainClass.getTime(),parsed.getArg(Function.SEND_MESSAGE.getParameters()[1]));
                break;
            case KICK:
                JOptionPane.showMessageDialog(null, "You have been kicked! \n" +
                                                                             "Reason: \"" + parsed.getArg(Function.KICK.getParameters()[1]) + "\"");
                System.exit(0);
                break;
            case PING:
                sendMessage(parser.ping(this));
                break;
            default:
                for (String line : lines) {
                    System.out.println(line);
                }
        }
        Main.mainClass.mainInterface.reload();
    }

}
