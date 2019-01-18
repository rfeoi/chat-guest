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
            String[] handshake = parser.doHandshake(this);
            for (String s : handshake){
                writer.println(s);
            }
            String[] usernameChange = parser.pushUsername(this, username);
            for (String s: usernameChange){
                writer.println(s);
            }
            writer.flush();
            Thread thread = new Thread(new ServerParser(socket));
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void sendMessage(String[] message) {
        for (String line:message) {
            writer.println(line);
        }
        writer.flush();
    }


    public void changeUsername(String newUsername) {
        String[] usernameChange = parser.pushUsername(this, newUsername);
        sendMessage(usernameChange);
    }

    void parsePackage(String[] lines) throws WrongPackageFormatException {
        Package parsed = parser.parsePackage(lines);
        switch (parsed.getFunction()) {
            case CHANGE_USERNAME:
                Main.mainClass.mainInterfaceData.changeUsername(parsed.getArg(Function.CHANGE_USERNAME.getParameters()[0]), parsed.getArg(Function.CHANGE_USERNAME.getParameters()[1]));
                break;
            case MOVE:
                //A client has been moved. (Where?)
                break;
            case INVITE:
                //You have been invited. (To what?)
                break;
            case CREATE_TEMP_CHANNEL:
                Main.mainClass.mainInterfaceData.addChannel(parsed.getArg(Function.CREATE_TEMP_CHANNEL.getParameters()[0]));
                break;
            case DELETE_TEMP_CHANNEL:
                Main.mainClass.mainInterfaceData.addChannel(parsed.getArg(Function.DELETE_TEMP_CHANNEL.getParameters()[0]));
                break;
        }
        Main.mainClass.mainInterface.reload();
    }


}
