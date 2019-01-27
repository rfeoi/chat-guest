package nspirep2p.application.server.install;

import nspirep2p.application.server.Main;
import nspirep2p.application.server.database.ChannelManagment;
import nspirep2p.application.server.database.Permission;
import nspirep2p.application.server.database.PermissionManagment;
import nspirep2p.application.server.database.ServerSetting;
import org.tmatesoft.sqljet.core.SqlJetException;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Scanner;

/**
 * Used for first time setup
 * Created by strifel on 16.11.2018.
 */
public class Installer {
    private HashMap<String, String> setupOptions;
    private HashMap<String, String> choosenOptions;

    public Installer() {
        choosenOptions = new HashMap<>();
        setupOptions = new HashMap<>();
        setupOptions.put("databaseType:SQLITE", "What Type would you like to have your Database?");
        setupOptions.put("port:24466", "Which port do you want to use?");
        setupOptions.put("slots:10", "How many slots do you wish?");
        setupOptions.put("generateChannel:1", "How many default channel would you like to generate?");
        setupOptions.put("adminPW:admin", "Which key do you want to have as an Admin Key (PLEASE DO NOT USE STANDARD KEY!)?");
        setupOptions.put("timeout:10000", "The time when a client should be kicked if he has not answered ping after x ms");
    }

    /**
     * Executes the setup
     */
    public void startSetup() {
        System.out.println("This is an installer!");
        System.out.println("Please follow the installer and answer the questions by typing in your wish and press enter.");
        System.out.println("By pressing just enter it takes the default setting (marked in brackets)");
        Scanner scanner = new Scanner(System.in);
        for (String atribute : setupOptions.keySet()) {
            String question = setupOptions.get(atribute);
            System.out.println(question + "[" + atribute.split(":")[1] + "]");
            String option = scanner.nextLine();
            if (!option.equals("")) {
                choosenOptions.put(atribute.split(":")[0], option);
            } else {
                choosenOptions.put(atribute.split(":")[0], atribute.split(":")[1]);
            }
        }
        createDatabase();
        Main.mainClass.permissionManagment = new PermissionManagment(Main.mainClass.databaseManager);
        Main.mainClass.channelManagment = new ChannelManagment(Main.mainClass.databaseManager);
        try {
            createDefaultRole(choosenOptions.get("adminPW"));
            createDefaultChannel(Integer.parseInt(choosenOptions.get("generateChannel")));
        } catch (SqlJetException | NoSuchAlgorithmException e) {
            System.err.println("An error happened during the creation of the roles");
            e.printStackTrace();
        }
    }

    private void createDefaultChannel(int count) throws SqlJetException {
        if (count > 0) {
            for (int i = 0; i < count; i++) {
                Main.mainClass.channelManagment.createNewChannel("Channel_" + i, count - i);
            }
        }
    }

    /**
     * Creates the database
     */
    private void createDatabase() {
        try {
            Main.mainClass.databaseManager.createTables();
            Main.mainClass.databaseManager.insertSetting(ServerSetting.SERVER_PORT, choosenOptions.get("port"));
            Main.mainClass.databaseManager.insertSetting(ServerSetting.SERVER_SLOTS, choosenOptions.get("slots"));
            Main.mainClass.databaseManager.insertSetting(ServerSetting.TIMEOUT_TIME_SINCE_REQUEST, choosenOptions.get("timeout"));
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates default roles
     */
    private void createDefaultRole(String adminPW) throws SqlJetException, NoSuchAlgorithmException {
        Main.mainClass.permissionManagment.createNewRole("user", new Permission[]{Permission.READ_CHANNEL, Permission.CREATE_TEMP_CHANNEL}, "");
        Main.mainClass.permissionManagment.createNewRole("admin", new Permission[]{Permission.READ_CHANNEL, Permission.CONTROL_OTHER, Permission.CREATE_TEMP_CHANNEL, Permission.KICK_USER, Permission.MANAGE_PUBLIC_CHANNEL, Permission.READ_UUID, Permission.JOIN_ANY, Permission.IMMUNE}, adminPW);
    }

}
