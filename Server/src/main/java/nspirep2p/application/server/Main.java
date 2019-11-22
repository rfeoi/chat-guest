package nspirep2p.application.server;

import nspirep2p.application.server.commandParser.Command;
import nspirep2p.application.server.commandParser.CommandParser;
import nspirep2p.application.server.connection.Client;
import nspirep2p.application.server.connection.ConnectionHandler;
import nspirep2p.application.server.database.*;
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
    public BanManagment banManagment;

    public static void main(String[] args) {
        System.out.println("[Server] Welcome to Chat-guest Server application!");
        System.out.println("[Server] This is an opensource project licensed under MIT License.");
        System.out.println("[Server] This is provided as it is without any warranty!");
        System.out.println("[Server] Feel free to contribute to https://github.com/rfeoi/chat-guest.");
        mainClass = new Main();
        try {
            mainClass.start();
        } catch (IOException e) {
            System.err.println("[Server] Oh! Something wrong happens when starting Server!");
            e.printStackTrace();
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("InfiniteLoopStatement")
    private void commandParsing(){
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (connectionHandler.getServerRun()) {
            try {
                String line = reader.readLine();
                if (line != null){
                    commandParser.insertManually(line);
                    if (commandParser.wasLastCorrect()){
                        try {
                            executeCommand();
                        } catch (Exception e) {
                            e.printStackTrace();
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
            case CCHANNEL:
                try {
                    channelManagment.createNewChannel(commandParser.getLastExecuted().getArgs()[0], Integer.parseInt(commandParser.getLastExecuted().getArgs()[1]));
                    channelManagment.reLoadDatabase();
                    System.out.println("[Server] Channel created!");
                    for (Client client : connectionHandler.getClients()) {
                        serverHandler.sendChannelsToClient(client);
                    }
                    System.out.println("[Server] Channels pushed");
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
            {
                Client client = serverHandler.getClientByUUID(commandParser.getLastExecuted().getArgs()[0]);
                if (client != null) {
                    System.out.println("[Server] Kicked " + client.username + " with uuid " + client.uuid + "!");
                    serverHandler.forceKick(client, reason);
                } else
                    System.err.println("[Server] Could not find user with uuid " + commandParser.getLastExecuted().getArgs()[0]);
                break;
            }
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
                        System.out.println("[Server] Command " + commandParser.getLastExecuted().getArgs()[0] + " does not exists!");
                    }
                }
                break;
            case KICKALL:
                String reason_kickall = "All have been kicked!";
                if (commandParser.getLastExecuted().getArgs() != null) {
                    reason_kickall = commandParser.getLastExecuted().getArgs()[0].replace("_", " ");
                }
                for (Client client : connectionHandler.getClients()) {
                    System.out.println("[Server] Kicking " + client.username + " with uuid " + client.uuid + "!");
                    serverHandler.forceKick(client, reason_kickall);
                }
                break;
            case CHANGEUSERNAME: {
                Client client = serverHandler.getClientByUUID(commandParser.getLastExecuted().getArgs()[0]);
                if (client != null) {
                    System.out.println("[Server] Changing username from " + client.username + " to " + commandParser.getLastExecuted().getArgs()[1]);
                    serverHandler.pushUsernameToClients(client, commandParser.getLastExecuted().getArgs()[1]);
                } else {
                    System.out.println("[Server] User not found!");
                }
            }
            break;
            case ALERTALL:
                String message = commandParser.getLastExecuted().getArgs()[0].replace("_", " ");
                for (Client client : connectionHandler.getClients()) {
                    serverHandler.sendErrorMessage(client, message);
                }
                System.out.println("[Server] Alerted all with " + message);
                break;
            case RENAMECHANNEL:
                try {
                    channelManagment.setName(commandParser.getLastExecuted().getArgs()[0], commandParser.getLastExecuted().getArgs()[1]);
                    channelManagment.reLoadDatabase();
                    System.out.println("[Server] Channel renamed!");
                    for (Client client : connectionHandler.getClients()) {
                        serverHandler.sendChannelsToClient(client);
                    }
                    System.out.println("[Server] Channels pushed");
                } catch (SqlJetException e) {
                    e.printStackTrace();
                }
                break;
            case SHOWUSERNAME: {
                Client client = serverHandler.getClientByUUID(commandParser.getLastExecuted().getArgs()[0]);
                if (client != null) {
                    System.out.println("[Server] The username of the client is: " + client.username);
                } else {
                    System.out.println("[Server] The client was not found!");
                }
            }
            break;
            case SHOWUUID: {
                Client client = serverHandler.getClientByUsername(commandParser.getLastExecuted().getArgs()[0]);
                if (client != null) {
                    System.out.println("[Server] The uuid of the client is: " + client.uuid);
                } else {
                    System.out.println("[Server] The client was not found!");
                }
            }
            break;
            case BAN: {
                Client client = serverHandler.getClientByUUID(commandParser.getLastExecuted().getArgs()[0]);
                if (client != null) {
                    long duration = 0;
                    if (commandParser.getLastExecuted().getArgs().length == 2) {
                        duration = Long.parseLong(commandParser.getLastExecuted().getArgs()[1]);
                    }
                    serverHandler.forceBan(client, duration);
                } else {
                    System.out.println("[Server] Client not found!");
                }
            }
            break;
            case UNBAN:
                serverHandler.forceUnban(commandParser.getLastExecuted().getArgs()[0]);
                break;
            case ADDPERM: {
                try {
                    Permission permission = Permission.valueOf(commandParser.getLastExecuted().getArgs()[1]);
                    permissionManagment.addPermission(commandParser.getLastExecuted().getArgs()[0], permission);
                    permissionManagment.reLoadDatabase();
                } catch (IllegalArgumentException e) {
                    System.out.println("[Server] Permission not found");
                } catch (SqlJetException e) {
                    e.printStackTrace();
                }
            }
            break;
            case REMOVEPERM: {
                try {
                    Permission permission = Permission.valueOf(commandParser.getLastExecuted().getArgs()[1]);
                    permissionManagment.removePermission(commandParser.getLastExecuted().getArgs()[0], permission);
                    permissionManagment.reLoadDatabase();
                } catch (IllegalArgumentException e) {
                    System.out.println("[Server] Permission not found");
                } catch (SqlJetException e) {
                    e.printStackTrace();
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
        System.out.println("[Server] Starting server...");
        System.out.print("[Server] Init ConnectionHandler");
        before = System.currentTimeMillis();
        connectionHandler = new ConnectionHandler(this, Integer.parseInt(databaseManager.getSetting(ServerSetting.SERVER_PORT)), Integer.parseInt(databaseManager.getSetting(ServerSetting.SERVER_SLOTS)));
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("[Server] Init server handler.");
        before = System.currentTimeMillis();
        serverHandler = new ServerHandler(this);
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("[Server] Init PermissionManagement");
        before = System.currentTimeMillis();
        permissionManagment = new PermissionManagment(databaseManager);
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("[Server] Init ChannelManagement");
        before = System.currentTimeMillis();
        channelManagment = new ChannelManagment(databaseManager);
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("[Server] Init BanManagment");
        before = System.currentTimeMillis();
        banManagment = new BanManagment(databaseManager);
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("[Server] Starting ConnectionHandler with Server.");
        before = System.currentTimeMillis();
        connectionHandler.start();
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("[Server] Starting CommandParser Instance.");
        before = System.currentTimeMillis();
        commandParser = new CommandParser();
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.println("[Server] Started on port " + databaseManager.getSetting(ServerSetting.SERVER_PORT));
        commandParsing();
    }
}
