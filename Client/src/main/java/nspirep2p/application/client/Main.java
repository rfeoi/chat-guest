package nspirep2p.application.client;

/*
  Main client class.
  Starts client
  Created by strifel on 05.11.2018.
 */

import nspirep2p.application.client.connection.ConnectionHandler;
import nspirep2p.application.client.fileHandling.UserPropetySave;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.v1.CommunicationParser;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class Main {
    private UserInterface userInterface;
    public MainInterface mainInterface;
    public ConnectionHandler connectionHandler;
    public UserPropetySave userPropetySave;
    public MainInterfaceData mainInterfaceData;
    public CommunicationParser communicationParser;

    public static Main mainClass;
    private String username, ip;

    public static void main(String[] args){
        mainClass = new Main();
        mainClass.start();
    }

    private Main(){
        userInterface = new UserInterface();
        connectionHandler = new ConnectionHandler();
        userPropetySave = new UserPropetySave();
        mainInterface = new MainInterface();
        mainInterfaceData = new MainInterfaceData();
        communicationParser = new CommunicationParser(ClientType.CLIENT);
    }

    /**
     * Starts the client
     */
    private void start() {
        String[] properties = new String[2];
        if (userPropetySave.hasConfigFile()) {
            try {
                properties = userPropetySave.getConfigFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            userInterface.startWithText(properties[1], properties[0]);
            username = properties[1];
            ip = properties[0];
        } else {
            userInterface.start();
        }
    }

    /**
     * Gets the current time
     * @return Returns the time as a String
     */
    public String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime());
    }

    /**
     * Gets the username
     * @return Returns the username
     */
    String getUsername() {
        return username;
    }

    /**
     * Gets the IP
     * @return Returns the IP
     */
    String getIP() {
        return ip;
    }
}
