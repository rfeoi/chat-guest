package nspirep2p.application.client;

import java.util.ArrayList;
import java.util.List;

public class MainInterfaceData {
    private List<String> users, channel;
    private String currentChannel;

    MainInterfaceData() {
        users = new ArrayList<>();
        channel = new ArrayList<>();
        currentChannel = "";
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

    String getUsers() {
        String returnString = "";
        for (String user: users) {
            returnString += user + "\n";
        }
        return returnString;
    }

    public void addChannel(String channelName) {
        channel.add(channelName);
    }

    public void removeChannel(String channelName) {
        channel.remove(channelName);
    }

    public void changeChannelName(String oldChannelName, String newChannelName) {
        int i = 0;
        for (String aChannel: channel) {
            if (aChannel.equals(oldChannelName)) {
                channel.remove(i);
                channel.add(i, newChannelName);
                break;
            }
            i = i + 1;
        }
    }

    String[] getChannel() {
        return channel.toArray(new String[0]);
    }

    public void setCurrentChannel(String channelName) {
        currentChannel = channelName;
    }

    public String getCurrentChannel() { return currentChannel; }

}
