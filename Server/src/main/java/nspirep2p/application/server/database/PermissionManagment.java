package nspirep2p.application.server.database;

import com.google.gson.Gson;
import nspirep2p.application.server.connection.Client;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

public class PermissionManagment {

    private HashMap<String, String[]> permissions;
    private HashMap<String, String> keys;
    private SqlJetDb database;
    private Gson gson = new Gson();

    /**
     * This class is the permission manager and needs an Database instance
     *
     * @param database the database Managing instance
     */
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

    /**
     * This will load or reload the permissions from database
     * @throws SqlJetException if an database error happens
     */
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

    /**
     * Checks if a user has a permissions
     * @param client which permissions should be checked
     * @param permission which should be checked
     */
    public void clientHasPermission(Client client, Permission permission){
        roleHasPermission(client.getRole(), permission);
    }

    /**
     * Checks if a role has a specified permission
     * @param role the name of the role which should be checked
     * @param permission the permission the role should be checked for
     * @return if the role has a specified permission
     */
    private boolean roleHasPermission(String role, Permission permission) {
        for (String perm : permissions.get(role)) {
            if (perm.equals(permission.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Tests if a role has a specified key (Checks with clearKey)
     *
     * @param keyClearText the key in clear text
     * @param role         the role of the user
     * @return if the key is right
     * @throws NoSuchAlgorithmException     Java error when generating md5
     * @throws UnsupportedEncodingException When happens error with utf 8 encoding
     */
    public boolean checkCleartextKey(String keyClearText, String role) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        String hashed = new String(MessageDigest.getInstance("MD5").digest(keyClearText.getBytes("UTF-8")), "UTF-8");
        return checkKey(hashed, role);
    }

    /**
     * Check if a hashed text is equals to the key in the database
     *
     * @param key  the hashed key
     * @param role the role which the key can be used
     * @return if the key is right
     */
    private boolean checkKey(String key, String role) {
        return keys.get(role).equals(key);
    }

}
