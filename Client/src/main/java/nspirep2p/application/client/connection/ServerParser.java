package nspirep2p.application.client.connection;

import nspirep2p.application.client.Main;
import nspirep2p.communication.protocol.v1.MultipleLinesReader;
import nspirep2p.communication.protocol.v1.WrongPackageFormatException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * It reads the data that it gets from the server
 * Created by robmroi03 on 03.12.2018.
 */
public class ServerParser implements Runnable{

    private Socket socket = null;
    private BufferedReader reader;
    private MultipleLinesReader multipleLinesReader;

    ServerParser (Socket socket) {
        try {
            this.socket = socket;
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        multipleLinesReader = new MultipleLinesReader();
    }

    /**
     * Constantly checks if the server is sending anything
     * and parses it then.
     */
    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                String message = reader.readLine();
                if (message == null) {
                    System.out.println("Closing down connection!");
                    break;
                }
                //Hier einfügen
                multipleLinesReader.read(message);
                if (multipleLinesReader.isEnd()) {
                    try {
                        //parse Package
                        Main.mainClass.connectionHandler.parsePackage(multipleLinesReader.getLines());
                    } catch (WrongPackageFormatException e) {
                        System.out.println("Wrong Package");
                    }
                    multipleLinesReader.clear();
                }
                //Main.mainClass.mainInterface.setNewMessage("Server", Main.mainClass.getTime(), message);
            } catch (IOException | NullPointerException e) {
                System.err.println("An Exception");
                System.out.println("Closing down connection!");
                e.printStackTrace();
                break;
            }
        }
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}
