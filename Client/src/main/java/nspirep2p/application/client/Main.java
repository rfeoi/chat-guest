package nspirep2p.application.client;

/**
 * Main client class.
 * Starts client
 * Created by strifel on 05.11.2018.
 */

import nspirep2p.application.client.connection.ConnectionHandler;
import nspirep2p.application.client.fileHandling.UserPropetySave;

import java.io.IOException;

/**
 * TODO:
 * GUI
 * Communication between server and TI
 * Tests(?)
 */
public class Main {
    private UserInterface userInterface;
    ConnectionHandler connectionHandler;
    public UserPropetySave userPropetySave;
    public static Main mainClass;

    public static void main(String[] args){
        mainClass = new Main();
        mainClass.start();
    }

    private Main(){
        userInterface = new UserInterface();
        connectionHandler = new ConnectionHandler();
        userPropetySave = new UserPropetySave();
    }

    private void start() {
        String[] properties = new String[2];
        if (userPropetySave.hasConfigFile()) {
            try {
                properties = userPropetySave.getConfigFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            userInterface.startWithText(properties[1], properties[0]);
        } else {
            userInterface.start();
        }
    }
}
