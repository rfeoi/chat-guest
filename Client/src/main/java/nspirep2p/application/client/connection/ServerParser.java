package nspirep2p.application.client.connection;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InterfaceAddress;
import java.net.Socket;

/**
 * It reads the data that it gets from the server
 * Created by robmroi03 on 03.12.2018.
 */
public class ServerParser implements Runnable{

    private Socket socket = null;
    private BufferedReader reader;

    ServerParser (Socket socket) {
        try {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                if (socket.getInputStream().read() == -1) {
                    System.out.println("Closing down connection!");
                    socket.close();
                }
                String message = reader.readLine();
                System.out.println(message);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        System.exit(0);
    }
}
