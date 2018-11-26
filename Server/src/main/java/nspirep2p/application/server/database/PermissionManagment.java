package nspirep2p.application.server.database;

import nspirep2p.application.server.connection.Client;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.internal.table.SqlJetTable;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.util.HashMap;

public class PermissionManagment {

    private HashMap<String, String[]> permissions;
    private HashMap<String, String> keys;
    private SqlJetDb database;
    public PermissionManagment(DatabaseManaging database){
        //Initialize variables
        permissions = new HashMap<String, String[]>();
        keys = new HashMap<String, String>();
        this.database = database.database;
        //Get options from database
        try {
            reLoadDatabase();
        } catch (SqlJetException e) {
            System.err.println("Could not load permissions. Giving no permissions. Caused by:");
            e.printStackTrace();
        }
    }

    private void reLoadDatabase() throws SqlJetException {
        database.beginTransaction(SqlJetTransactionMode.READ_ONLY);
        ISqlJetTable table = database.getTable("roles");
    }

    public void clientHasPermission(Client client, Permission permission){

    }

}
