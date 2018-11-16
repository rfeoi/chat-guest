package nspirep2p.application.server.database;

/**
 * Used to create a communication between server and a Database
 * Created by strifel on 16.11.2018.
 */
public class DatabaseManaging {

    public DatabaseManaging() {
        // SqlJetDb db = SqlJetDb.open(dbFile, true);
    }

    /**
     * Simply returns if a Database is found and configured properly
     *
     * @return if it is installed
     */
    public boolean isInstalled() {
        return false;
    }

    /**
     * Return setting of settings table
     *
     * @param arg Name of setting
     * @return value
     */
    public String getSetting(ServerSetting arg) {
        return null;
    }
}
