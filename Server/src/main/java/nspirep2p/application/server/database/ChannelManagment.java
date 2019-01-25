package nspirep2p.application.server.database;


import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;


public class ChannelManagment {

    private String[] channel;
    private SqlJetDb database;

    /**
     * This class is the permission manager and needs an Database instance
     *
     * @param database the database Managing instance
     */
    public ChannelManagment(DatabaseManaging database) {
        //Initialize variables
        this.database = database.database;
        //Get options from database
        try {
            reLoadDatabase();
        } catch (SqlJetException e) {
            System.err.println("Could not load channels. Caused by:");
            e.printStackTrace();
        }
    }

    /**
     * This will load or reload the permissions from database
     *
     * @throws SqlJetException if an database error happens
     */
    private void reLoadDatabase() throws SqlJetException {
        database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
        ISqlJetTable table = database.getTable("channel");
        ISqlJetCursor cursor = table.order(table.getPrimaryKeyIndexName());
        try {
            if (!cursor.eof()) {
                LinkedHashMap<Integer, String> unsortedChannel = new LinkedHashMap<>();
                do {
                    int value = (int) cursor.getInteger("level");
                    unsortedChannel.put(value, cursor.getString("name"));
                } while (cursor.next());
                LinkedList<String> sorted = new LinkedList<>();
                unsortedChannel.entrySet().stream()
                        .sorted(Map.Entry.comparingByKey())
                        .forEach(entry -> sorted.add(entry.getValue()));
                channel = sorted.toArray(new String[0]);
            }
        } finally {
            cursor.close();
        }

    }


    /**
     * Creates a new role
     *
     * @param name the name of the role
     * @param prio the prio a channel has
     * @throws SqlJetException The exception for anything went wrong
     */
    public void createNewChannel(String name, int prio) throws SqlJetException {
        database.beginTransaction(SqlJetTransactionMode.WRITE);
        ISqlJetTable table = database.getTable("channel");
        try {
            table.insert(name, prio);
        } finally {
            database.commit();
        }

    }

    public String[] getChannel() {
        return channel;
    }
}
