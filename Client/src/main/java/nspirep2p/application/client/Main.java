package nspirep2p.application.client;

/**
 * Main client class.
 * Starts client
 * Created by strifel on 05.11.2018.
 */

import nspirep2p.application.client.connection.ConnectionHandler;
import nspirep2p.application.client.fileHandling.UserPropetySave;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * TODO:
 * GUI
 * Communication between server and TI
 * Tests(?)
 */
public class Main {
    private UserInterface userInterface;
    public MainInterface mainInterface;
    ConnectionHandler connectionHandler;
    public UserPropetySave userPropetySave;
    public static Main mainClass;
    private String username;

    public static void main(String[] args){
        mainClass = new Main();
        mainClass.start();
    }

    private Main(){
        userInterface = new UserInterface();
        connectionHandler = new ConnectionHandler();
        userPropetySave = new UserPropetySave();
        mainInterface = new MainInterface();
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
            username = properties[1];
        } else {
            userInterface.start();
        }
    }


    public String getTime() {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        return sdf.format(cal.getTime());
    }

    public String getUsername() {
        return username;
    }
}
