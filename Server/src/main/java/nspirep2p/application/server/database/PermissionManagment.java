package nspirep2p.application.server.database;

import com.google.gson.Gson;
import nspirep2p.application.server.connection.Client;
import org.tmatesoft.sqljet.core.SqlJetException;
import org.tmatesoft.sqljet.core.SqlJetTransactionMode;
import org.tmatesoft.sqljet.core.table.ISqlJetCursor;
import org.tmatesoft.sqljet.core.table.ISqlJetTable;
import org.tmatesoft.sqljet.core.table.SqlJetDb;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

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
        permissions = new HashMap<>();
        keys = new HashMap<>();
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
    public void reLoadDatabase() throws SqlJetException {
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
    public boolean clientHasPermission(Client client, Permission permission){
        return roleHasPermission(client.getRole(), permission);
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
     * Check if a hashed text is equals to the key in the database
     *
     * @param key  the hashed key
     * @param role the role which the key can be used
     * @return if the key is right
     */
    private boolean checkKey(String key, String role) {
        if (role != null && keys.containsKey(role)) {
            return keys.get(role).equals(key);
        }
        return false;
    }


    /**
     * Checks the key
     *
     * @param key (hashed) key
     * @return role, if wrong returns standard role (user)
     */
    public String checkKey(String key) {
        for (String role : keys.keySet()) {
            if (checkKey(key, role)) {
                return role;
            }
        }
        return "user";
    }


    /**
     * Creates a new role
     *
     * @param name        the name of the role
     * @param permissions the permissions as array of the role
     * @param key         the key used to get the role
     * @throws SqlJetException              The exception for anything went wrong
     * @throws NoSuchAlgorithmException     If something with keying went wrong
     */
    public void createNewRole(String name, Permission[] permissions, String key) throws SqlJetException, NoSuchAlgorithmException {
        String hashed = Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(key.getBytes()));
        database.beginTransaction(SqlJetTransactionMode.WRITE);
        ISqlJetTable table = database.getTable("roles");
        String json = gson.toJson(permissions, Permission[].class);
        try {
            table.insert(name, json, hashed);
        } finally {
            database.commit();
        }

    }

    /**
     * Sets the permissions of a group
     *
     * @param group       the group where the permissions should be set
     * @param permissions the permissions
     */
    private void setGroupPermissions(String group, String[] permissions) {
        String json_permission = gson.toJson(permissions);
        try {
            database.beginTransaction(SqlJetTransactionMode.WRITE);
            ISqlJetCursor updateCursor = database.getTable("roles").open();
            do {
                if (updateCursor.getString("name").equals(group)) {
                    updateCursor.update(
                            group,
                            json_permission,
                            updateCursor.getString("key")
                    );
                    break;
                }

            } while (updateCursor.next());
            updateCursor.close();
            database.commit();
        } catch (SqlJetException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds permission to group
     *
     * @param group      the name of the role
     * @param permission the permission which should be added
     */
    @SuppressWarnings("Duplicates") //Coming from removePermission
    public void addPermission(String group, Permission permission) {
        if (permissions.get(group) == null) return;
        List<String> permissions_group = new LinkedList<>(Arrays.asList(permissions.get(group)));
        permissions_group.add(permission + "");
        String[] permission_group_array = permissions_group.toArray(new String[0]);
        setGroupPermissions(group, permission_group_array);
    }

    /**
     * Removes permission from group
     *
     * @param group      the name of the role
     * @param permission the permission which should be remove
     */
    @SuppressWarnings("Duplicates") //Coming from addPermission
    public void removePermission(String group, Permission permission) {
        if (permissions.get(group) == null) return;
        List<String> permissions_group = new LinkedList<>(Arrays.asList(permissions.get(group)));
        permissions_group.remove(permission + "");
        String[] permission_group_array = permissions_group.toArray(new String[0]);
        setGroupPermissions(group, permission_group_array);
    }


}
