package nspirep2p.application.server.database;

import com.google.gson.Gson;
import nspirep2p.application.server.connection.Client;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.util.HashMap;

public class PermissionManagment {

    private HashMap<String, String[]> permissions;
    private HashMap<String, String> keys;
    private SqlJetDb database;
    private Gson gson = new Gson();
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
        ISqlJetCursor cursor = table.order(table.getPrimaryKeyIndexName());
        try {
            if (!cursor.eof()) {
                do {
                    String role = cursor.getString("name");
                    String[] permissions = gson.fromJson(cursor.getString("permissions"), String[].class);
                    this.permissions.put(role, permissions);
                    keys.put(role, cursor.getString("key"));
                } while (cursor.next());
            }
        } finally {
            cursor.close();
        }

    }

    public void clientHasPermission(Client client, Permission permission){
        roleHasPermission(client.getRole(), permission);
    }

    private boolean roleHasPermission(String role, Permission permission) {
        for (String perm : permissions.get(role)) {
            if (perm.equals(permission.toString())) {
                return true;
            }
        }
        return false;
    }

}
