package nspirep2p.application.server;

import nspirep2p.application.server.connection.ConnectionHandler;
import nspirep2p.application.server.database.DatabaseManaging;
import nspirep2p.application.server.database.PermissionManagment;
import nspirep2p.application.server.database.ServerSetting;
import nspirep2p.application.server.install.Installer;
import org.tmatesoft.sqljet.core.SqlJetException;

import java.io.IOException;

/**
 * Main server class.
 * Starts server
 * Created by strifel on 05.11.2018.
 */
public class Main {
    public DatabaseManaging databaseManager;
    public ServerHandler serverHandler;
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
        System.out.print("Init server handler.");
        before = System.currentTimeMillis();
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        serverHandler = new ServerHandler(this);
        System.out.print("Init PermissionManagment");
        before = System.currentTimeMillis();
        permissionManagment = new PermissionManagment(databaseManager);
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("Init ConnectionHandler");
        before = System.currentTimeMillis();
        connectionHandler = new ConnectionHandler(this, Integer.parseInt(databaseManager.getSetting(ServerSetting.SERVER_PORT)), Integer.parseInt(databaseManager.getSetting(ServerSetting.SERVER_SLOTS)));
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
        System.out.print("Starting ConnectionHandler with Server.");
        before = System.currentTimeMillis();
        connectionHandler.start();
        System.out.println("[" + (System.currentTimeMillis() - before) + "ms]");
    }
}
