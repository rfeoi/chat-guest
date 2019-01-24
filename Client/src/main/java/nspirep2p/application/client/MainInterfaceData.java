package nspirep2p.application.client;

import java.util.ArrayList;
import java.util.List;

public class MainInterfaceData {
    private List<String> users, channel;
    private String currentChannel;
    private boolean hasCreatedTempChannel;

    MainInterfaceData() {
        users = new ArrayList<>();
        channel = new ArrayList<>();
        currentChannel = "";
        hasCreatedTempChannel = false;
    }

    public void addUser(String username) {
        users.add(username);
    }

   public void removeUser(String username) {
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

    String[] getUsers() {
        return users.toArray(new String[0]);
    }

    public void addChannel(String channelName) {
        channel.add(channelName);
    }

    public void removeChannel(String channelName) {
        channel.remove(channelName);
    }

    String[] getChannel() {
        return channel.toArray(new String[0]);
    }

    void setCurrentChannel(String channelName) {
        currentChannel = channelName;
    }

    public String getCurrentChannel() {
        return currentChannel;
    }

    void setHasCreatedTempChannel() {
        this.hasCreatedTempChannel = true;
    }

    boolean getHasCreatedTempChannel() { return !hasCreatedTempChannel; }

    public boolean userIsInYourChannel(String username) {
        for (String user: users) {
            if (user.equals(username)) {
                return true;
            }
        }
        return false;
    }

}
