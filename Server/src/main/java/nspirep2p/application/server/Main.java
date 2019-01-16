package nspirep2p.application.server;

import nspirep2p.application.server.commandParser.CommandParser;
import nspirep2p.application.server.connection.ConnectionHandler;
import nspirep2p.application.server.database.DatabaseManaging;
import nspirep2p.application.server.database.PermissionManagment;
import nspirep2p.application.server.database.ServerSetting;
import nspirep2p.application.server.install.Installer;
import org.tmatesoft.sqljet.core.SqlJetException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Main server class.
 * Starts server
 * Created by strifel on 05.11.2018.
 */
public class Main {
    public DatabaseManaging databaseManager;
    public ServerHandler serverHandler;
    public CommandParser commandParser;
    ConnectionHandler connectionHandler;
    public PermissionManagment permissionManagment;
    public static Main mainClass;

    public static void main(String[] args) {
        System.out.println("Welcome to NSpireP2P Server application!");
        System.out.println("This is an opensource project licensed under MIT License.");
        System.out.println("This is provided as it is without any warranty!");
        System.out.println("Feel free to contribute to https://github.com/strifel/nspire-p2p.");
        mainClass = new Main();
        try {
            mainClass.start();
        } catch (IOException e) {
            System.err.println("Oh! Something wrong happens when starting Server!");
            e.printStackTrace();
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }

    private void commandParsing(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            try {
                String line = reader.readLine();
                if (line != null){
                    commandParser.insertManually(line);
                    if (commandParser.wasLastCorrect()){
                        executeCommand();
                    }else{
                        if (commandParser.getLastExecuted().getError().equals("TOOLESSPARAMETER")){
                            System.out.println("Wrong command");
                            System.out.println("Use: " + commandParser.getLastExecuted().getCommand().getUsage());
                        }else{
                            System.out.println("Wrong command");
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void executeCommand(){
        switch (commandParser.getLastExecuted().getCommand()){
            case STOP:
                System.exit(0);
                break;
        }
    }

    private Main() {
        try {
            databaseManager = new DatabaseManaging();
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }

    private void start() throws IOException, SqlJetException {
        if (!databaseManager.isInstalled()) {
            Installer installer = new Installer();
            installer.startSetup();
        }
        long before;
        System.out.println("Starting server...");
        System.out.print("Init ConnectionHandler");
        before = System.currentTimeMillis();
        connectionHandler = new ConnectionHandler(this, Integer.parseInt(databaseManager.getSetting(ServerSetting.SERVER_PORT)), Integer.parseInt(databaseManager.getSetting(ServerSetting.SERVER_SLOTS)));
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        before = System.currentTimeMillis();
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("Init server handler.");
        serverHandler = new ServerHandler(this);
        System.out.print("Init PermissionManagment");
        before = System.currentTimeMillis();
        permissionManagment = new PermissionManagment(databaseManager);
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("Starting ConnectionHandler with Server.");
        before = System.currentTimeMillis();
        connectionHandler.start();
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("Starting CommandParser Instance.");
        before = System.currentTimeMillis();
        commandParser = new CommandParser();
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.println("Started on port " + databaseManager.getSetting(ServerSetting.SERVER_PORT));
        commandParsing();
    }
}
