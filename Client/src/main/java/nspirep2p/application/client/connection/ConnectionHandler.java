package nspirep2p.application.client.connection;

/**
 * Created by robmroi03 on 12.11.2018.
 * Handles the connection between client and server
 */
public class ConnectionHandler {

    /**
     * tries to connect to the server
     * @param ip ipAddress of the server
     * @param username username
     * @return if the connection has succeeded
     */
    public boolean connect(String ip, String username){
        System.out.println("IP: " + ip);
        System.out.println("username: " + username);
        //connection has failed:
        return false;
        //connection succeeded:
        //return true;
        //TODO sockets and some fun
    }
}
