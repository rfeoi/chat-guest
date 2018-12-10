package nspirep2p.application.server.install;

import nspirep2p.application.server.Main;
import nspirep2p.application.server.database.ServerSetting;
import org.tmatesoft.sqljet.core.SqlJetException;

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
        choosenOptions = new HashMap<String, String>();
        setupOptions = new HashMap<String, String>();
        setupOptions.put("databaseType:SQLITE", "What Type would you like to have your Database?");
        setupOptions.put("port:24466", "Which port do you want to use?");
        setupOptions.put("slots:10", "How many slots do you wish?");
        setupOptions.put("generateChannel:0", "How many default channel would you like to generate?");
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
    }

    /**
     * Creates the database
     */
    private void createDatabase() {
        try {
            Main.mainClass.databaseManager.createTables();
            Main.mainClass.databaseManager.insertSetting(ServerSetting.SERVER_PORT, choosenOptions.get("port"));
            Main.mainClass.databaseManager.insertSetting(ServerSetting.SERVER_SLOTS, choosenOptions.get("slots"));
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates default roles
     */
    private void createDefaultRole() {
        //TODO
    }
}
