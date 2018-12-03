package nspirep2p.application.server.database;

import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.io.File;

/**
 * Used to create a communication between server and a Database
 * Created by strifel on 16.11.2018.
 */
public class DatabaseManaging {

    SqlJetDb database;

    public DatabaseManaging() throws SqlJetException {
        File dbFile = new File("database.sqlite");
        database = SqlJetDb.open(dbFile, true);
    }

    /**
     * Simply returns if a Database is found and configured properly
     *
     * @return if it is installed
     */
    //TODO
    public boolean isInstalled() {
        return false;
    }


    /**
     * Creates tables in Database
     * Creates Table:
     *  - settings (to save settings)
     *  - roles (for permissions system)
     * @throws SqlJetException if sql has an error
     */
    public void createTables() throws SqlJetException {
        //  database.getOptions().setAutovacuum(true);
        database.beginTransaction(SqlJetTransactionMode.WRITE);
        try {
            database.getOptions().setUserVersion(1);
        } finally {
            database.commit();
        }
        String settingsTable = "CREATE TABLE settings (setting TEXT NOT NULL PRIMARY KEY , value TEXT)";
        //String settingsIndex = "CREATE INDEX value ON settings(value)";
        String settingsIndex2 = "CREATE INDEX setting ON settings(setting)";
        String rolesTable = "CREATE TABLE roles (name TEXT NOT NULL PRIMARY KEY, key TEXT, permissions TEXT)";
        try {
            database.createTable(settingsTable);
            database.createTable(rolesTable);
            database.createIndex(settingsIndex2);
        } finally {
            database.commit();
        }
    }
    /**
     * Return setting of settings table
     *
     * @param arg Name of setting
     * @return value
     */
    public String getSetting(ServerSetting arg) throws SqlJetException {
        database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
        return database.getTable("settings").lookup("setting", arg.toString()).getString("value");
    }

    /**
     * Sets the value of a setting
     *
     * @param arg   the name of the setting
     * @param value the value of the setting
     * @throws SqlJetException if something with database goes wrong
     */
    public void setSetting(ServerSetting arg, String value) throws SqlJetException {
        database.beginTransaction(SqlJetTransactionMode.WRITE);
        ISqlJetCursor updateCursor = database.getTable("settings").open();
        do {
            if (updateCursor.getString("setting").equals(arg.toString())) {
                updateCursor.update(
                        updateCursor.getValue("setting"),
                        value);
            }

        } while (updateCursor.next());
        updateCursor.close();
        database.commit();
    }

    /**
     * Insert a new setting to database (primarily used by installer)
     *
     * @param arg   the name of the setting
     * @param value the value of the setting
     * @throws SqlJetException Exception when something strangs happens with data
     */
    public void insertSetting(ServerSetting arg, String value) throws SqlJetException {
        try {
            database.getTable("settings").insert(arg.toString(), value);
        } finally {
            database.commit();
        }
    }
}
