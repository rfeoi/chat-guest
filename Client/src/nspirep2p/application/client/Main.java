package nspirep2p.application.client;

/**
 * Main client class.
 * Starts client
 * Created by strifel on 05.11.2018.
 */

import nspirep2p.application.client.connection.ConnectionHandler;

/**
 * TODO:
 * GUI
 * Communication between server and TI
 * Tests(?)
 */
public class Main {
    private UserInterface userInterface;
    public ConnectionHandler connectionHandler;
    static Main mainClass;

    public static void main(String[] args){
        mainClass = new Main();
        mainClass.start();
    }

    private Main(){
        userInterface = new UserInterface();
        connectionHandler = new ConnectionHandler();
    }

    private void start() {
        userInterface.start();
    }
}
