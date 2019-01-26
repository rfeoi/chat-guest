package nspirep2p.application.server;

import nspirep2p.application.server.commandParser.Command;
import nspirep2p.application.server.commandParser.CommandParser;
import nspirep2p.application.server.connection.Client;
import nspirep2p.application.server.connection.ConnectionHandler;
import nspirep2p.application.server.database.ChannelManagment;
import nspirep2p.application.server.database.DatabaseManaging;
import nspirep2p.application.server.database.PermissionManagment;
import nspirep2p.application.server.database.ServerSetting;
import nspirep2p.application.server.install.Installer;
import org.tmatesoft.sqljet.core.SqlJetException;

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
    public ChannelManagment channelManagment;
    private CommandParser commandParser;
    ConnectionHandler connectionHandler;
    public PermissionManagment permissionManagment;
    public static Main mainClass;

    public static void main(String[] args) {
        System.out.println("Welcome to Chat-guest Server application!");
        System.out.println("This is an opensource project licensed under MIT License.");
        System.out.println("This is provided as it is without any warranty!");
        System.out.println("Feel free to contribute to https://github.com/rfeoi/chat-guest.");
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

    @SuppressWarnings("InfiniteLoopStatement")
    private void commandParsing(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true){
            try {
                String line = reader.readLine();
                if (line != null){
                    commandParser.insertManually(line);
                    if (commandParser.wasLastCorrect()){
                        try {
                            executeCommand();
                        } catch (Exception e) {
                            System.out.println("Something went wrong");
                        }
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
                try {
                    connectionHandler.stop();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
                break;
            case CCHANEL:
                try {
                    channelManagment.createNewChannel(commandParser.getLastExecuted().getArgs()[0], Integer.parseInt(commandParser.getLastExecuted().getArgs()[1]));
                    System.out.println("Channel created!");
                } catch (SqlJetException e) {
                    e.printStackTrace();
                } catch (NumberFormatException e) {
                    System.out.println("Use: " + commandParser.getLastExecuted().getCommand().getUsage());
                }
                break;
            case LISTCLIENTS:
                for (Client client : connectionHandler.getClients()) {
                    System.out.println(client.username + "   " + client.uuid);
                }
                break;
            case KICK:
                String reason = "Kicked by an admin";
                if (commandParser.getLastExecuted().getArgs().length == 2) {
                    reason = commandParser.getLastExecuted().getArgs()[1].replace("_", " ");
                }
                Client client = serverHandler.getClientByUUID(commandParser.getLastExecuted().getArgs()[0]);
                if (client != null) {
                    System.out.println("Kicked " + client.username + " with uuid " + client.uuid + "!");
                    serverHandler.forceKick(client, reason);
                } else
                    System.err.println("Could not find user with uuid " + commandParser.getLastExecuted().getArgs()[0]);
                break;
            case HELP:
                if (commandParser.getLastExecuted().getArgs() == null) {
                    for (Command command : Command.values()) {
                        if (command.getHelp() != null) {
                            System.out.println(command.getHelp());
                        }
                    }
                } else {
                    try {
                        Command helpWanted = Command.valueOf(commandParser.getLastExecuted().getArgs()[0].toUpperCase());
                        if (helpWanted.getHelp() != null) {
                            System.out.println(helpWanted.getHelp());
                        } else {
                            System.out.println(helpWanted.getParent().getHelp());
                        }
                    } catch (IllegalArgumentException e) {
                        System.out.println("Command " + commandParser.getLastExecuted().getArgs()[0] + " does not exists!");
                    }
                }
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
        System.out.print("Init server handler.");
        before = System.currentTimeMillis();
        serverHandler = new ServerHandler(this);
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("Init PermissionManagement");
        before = System.currentTimeMillis();
        permissionManagment = new PermissionManagment(databaseManager);
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("Init ChannelManagement");
        before = System.currentTimeMillis();
        channelManagment = new ChannelManagment(databaseManager);
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
