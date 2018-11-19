package nspirep2p.application.server;

import nspirep2p.application.server.database.DatabaseManaging;
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
    private ConnectionHandler connectionHandler;
    public static Main mainClass;

    public static void main(String[] args) {
        System.out.println("Welcome to NSpireP2P Server application!");
        System.out.println("This is an opensource project licensed under MIT License");
        System.out.println("This is provided as it is without any warranty!");
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
        if (databaseManager.isInstalled()) {
            connectionHandler = new ConnectionHandler(Integer.parseInt(databaseManager.getSetting(ServerSetting.SERVER_PORT)));
        } else {
            Installer installer = new Installer();
            installer.startSetup();
        }
    }
}
