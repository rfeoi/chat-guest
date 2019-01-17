package nspirep2p.application.client;

import java.util.ArrayList;
import java.util.List;

public class MainInterfaceData {
    private List<String> users;

    public MainInterfaceData() {
        users = new ArrayList<String>();
    }

    void addUser(String username) {
        users.add(username);
    }

    void removeUser(String username) {
        users.remove(username);
    }

    public void changeUsername(String oldUsername, String newUsername) {
        int i = 0;
        for (String user: users) {
            if (user.equals(oldUsername)) {
                users.remove(i);
                users.add(i, newUsername);
                break;
            }
            i++;
        }
    }

    String getUsers() {
        String returnString = "";
        for (String user: users) {
            returnString += user + "\n";
        }
        return returnString;
    }

}
