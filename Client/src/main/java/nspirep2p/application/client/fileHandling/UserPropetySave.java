package nspirep2p.application.client.fileHandling;

import com.google.gson.Gson;

import java.io.*;

/**
 * Created by robmroi03 on 16.11.2018.
 * saves the user data
 */
public class UserPropetySave {
    //will save the user data in the future
    private Gson gson = new Gson();

    public void generateConfigFile(String ip, String username, String uuid) throws IOException {
        File file = new File(System.getProperty("user.home") + "/.chat_guest_login_config.json");
        if (file.exists()) file.delete();
        String[] properties = new String[]{ip, username, uuid};
        String rawJson = gson.toJson(properties);
        BufferedWriter writer = new BufferedWriter(new FileWriter(file));
        writer.write(rawJson);
        writer.close();
    }


    public boolean hasConfigFile() {
        File file = new File(System.getProperty("user.home") + "/.chat_guest_login_config.json");
        return file.exists();
    }

    public String[] getConfigFile() throws IOException {
        File file = new File(System.getProperty("user.home") + "/.chat_guest_login_config.json");
        BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
        return gson.fromJson(bufferedReader.readLine(), String[].class);
    }
}
