package nspirep2p.application.server.database;


import nspirep2p.application.server.connection.Client;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.util.Date;
import java.util.HashMap;


public class BanManagment {

    private HashMap<String, Long> bans;
    private SqlJetDb database;

    /**
     * This class is the permission manager and needs an Database instance
     *
     * @param database the database Managing instance
     */
    public BanManagment(DatabaseManaging database) {
        //Initialize variables
        bans = new HashMap<>();
        this.database = database.database;
        //Get bans from database
        try {
            reLoadDatabase();
        } catch (SqlJetException e) {
            System.err.println("Could not load bans. Caused by:");
            e.printStackTrace();
        }
    }

    /**
     * This will load or reload the bans, and removes the none needed
     *
     * @throws SqlJetException if an database error happens
     */
    public void reLoadDatabase() throws SqlJetException {
        database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
        ISqlJetTable table = database.getTable("bans");
        ISqlJetCursor cursor = table.order(table.getPrimaryKeyIndexName());
        try {
            if (!cursor.eof()) {
                do {
                    long end = cursor.getInteger("end");
                    if (end != 0 && new Date().getTime() > end) {

                    } else {
                        bans.put(cursor.getString("uuid"), end);
                    }
                } while (cursor.next());
            }
        } finally {
            cursor.close();
        }

    }


    /**
     * Creates a new role
     *
     * @param client the client
     * @param time   the timestamp a user should be unbanned (0 if never)
     * @throws SqlJetException The exception for anything went wrong
     */
    public void ban(Client client, long time) {
        try {
            database.beginTransaction(SqlJetTransactionMode.WRITE);
            ISqlJetTable table = database.getTable("bans");
            try {
                table.insert(client.uuid, time);
            } finally {
                database.commit();
            }
            bans.put(client.uuid, time);
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the value of a setting
     *
     * @param uuid the uuid of the user
     * @throws SqlJetException if something with database goes wrong
     */
    public void unban(String uuid) {
        try {
            database.beginTransaction(SqlJetTransactionMode.WRITE);
            ISqlJetCursor updateCursor = database.getTable("bans").open();
            do {
                if (updateCursor.getString("uuid").equals(uuid)) {
                    updateCursor.delete();
                    break;
                }

            } while (updateCursor.next());
            updateCursor.close();
            database.commit();
            bans.remove(uuid);
        } catch (SqlJetException e) {
            e.printStackTrace();
        }

    }

    /**
     * Check if a user is banned
     *
     * @param client the client shich should be checked
     * @return if the user is banned
     */
    public boolean isBanned(Client client) {
        if (!bans.containsKey(client.uuid)) {
            return false;
        } else {
            if (new Date().getTime() > bans.get(client.uuid) && bans.get(client.uuid) != 0) {
                unban(client.uuid);
                return false;
            } else {
                return true;
            }

        }
    }
}
