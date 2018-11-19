package nspirep2p.application.server.install;

import GLOOP.Sys;
import nspirep2p.application.server.Main;
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
        setupOptions.put("databaseType:SQLITE", "What Type would you like to have your Database?[SQLITE]");
        setupOptions.put("generateChannel:1", "How many default channel would you like to generate?[1]");
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
            System.out.println(question);
            String option = scanner.nextLine();
            if (option != null) {
                choosenOptions.put(atribute.split("=")[0], option);
            } else {
                choosenOptions.put(atribute.split("=")[0], atribute.split("=")[1]);
            }
        }

    }

    /**
     * Creates the database
     *
     * @param choosenOptions by user
     */
    public void createDatabase(HashMap<String, String> choosenOptions) {
        try {
            Main.mainClass.databaseManager.createTables();
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }
}
