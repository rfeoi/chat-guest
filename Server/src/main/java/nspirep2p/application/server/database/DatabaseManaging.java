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

    private SqlJetDb database;

    public DatabaseManaging() throws SqlJetException {
        File dbFile = new File("database.sqlite");
        database = SqlJetDb.open(dbFile, true);
    }

    /**
     * Simply returns if a Database is found and configured properly
     *
     * @return if it is installed
     */
    public boolean isInstalled() {
        return false;
    }


    public void createTables() throws SqlJetException {
        database.getOptions().setAutovacuum(true);
        database.beginTransaction(SqlJetTransactionMode.WRITE);
        try {
            database.getOptions().setUserVersion(1);
        } finally {
            database.commit();
        }
        String settingsTable = "CREATE TABLE settings (setting TEXT NOT NULL PRIMARY KEY , value TEXT)";
        try {
            database.createTable(settingsTable);
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

    public void insertSetting(ServerSetting arg, String value) throws SqlJetException {
        try {
            database.getTable("settings").insert(arg, value);
        } finally {
            database.commit();
        }
    }
}
