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

    /**
     * Adds a user to the arrayList.
     * @param username the username
     */
    public void addUser(String username) {
        if (username.equals("null")) { return; }
        users.add(username);
    }

    /**
     * Removes user by their username
     * @param username the user that should be deleted
     */
    public void removeUser(String username) {
        users.remove(username);
    }

    /**
     * Changes the name of the user in the arrayList
     * @param oldUsername the name that is in the arrayList
     * @param newUsername the new username
     */
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

    /**
     * Gets the arrayList as an array
     * @return Returns it
     */
    String[] getUsers() {
        return users.toArray(new String[0]);
    }

    /**
     * Removes all users
     */
    public void removeAllUsers() {
        for (int i = 0; i<users.size(); i++) {
            users.remove(i);
        }
    }

    /**
     * Adds a channel to the arrayList
     * @param channelName the name of the channel
     */
    public void addChannel(String channelName) {
        channel.add(channelName);
    }

    /**
     * Removes channel by their name
     * @param channelName the channel that should be deleted
     */
    public void removeChannel(String channelName) {
        channel.remove(channelName);
    }

    /**
     * Gets the arrayList as an array
     * @return Returns it
     */
    String[] getChannel() {
        return channel.toArray(new String[0]);
    }

    public void removeAllChannel() {
        for (int i=0; i<channel.size(); i++) {
            channel.remove(i);
        }
    }

    /**
     * Sets the current channel
     * @param channelName the name of the current channel
     */
    void setCurrentChannel(String channelName) {
        currentChannel = channelName;
    }

    public void changeChannelName(String oldName, String newName) {
        int i = 0;
        for (String aChannel: channel) {
            if (aChannel.equals(oldName)) {
                channel.remove(i);
                channel.add(i, newName);
                break;
            }
            i++;
        }
    }

    /**
     * Gets the current channel
     * @return returns it
     */
    public String getCurrentChannel() {
        return currentChannel;
    }

    /**
     * Sets that the user has created a temporary channel
     */
    void setHasCreatedTempChannel() {
        this.hasCreatedTempChannel = true;
    }

    /**
     * Returns if the user has created a temporary channel
     * @return the opposite
     */
    boolean getHasCreatedTempChannel() {
        return !hasCreatedTempChannel;
    }

    /**
     * Checks if a user is in your channel
     * @param username the name of the user
     * @return if the user is in the arrayList
     */
    public boolean userIsInYourChannel(String username) {
        for (String user: users) {
            if (user.equals(username)) {
                return true;
            }
        }
        return false;
    }

}
