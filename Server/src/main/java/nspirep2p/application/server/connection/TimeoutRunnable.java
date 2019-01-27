package nspirep2p.application.server.connection;

import nspirep2p.application.server.database.ServerSetting;
import org.tmatesoft.sqljet.core.SqlJetException;

import java.util.Date;

public class TimeoutRunnable implements Runnable {
    private int timeout_time_since_request;
    private ConnectionHandler connectionHandler;

    TimeoutRunnable(ConnectionHandler connectionHandler) {
        this.connectionHandler = connectionHandler;
        try {
            this.timeout_time_since_request = Integer.parseInt(connectionHandler.main.databaseManager.getSetting(ServerSetting.TIMEOUT_TIME_SINCE_REQUEST));
        } catch (SqlJetException | NumberFormatException e) {
            System.err.println("Could not read timeout time. Set it to 10000");
            timeout_time_since_request = 10000;
        }
    }

    @Override
    public void run() {
        long checkStarted;
        while (connectionHandler.serverRun) {
            checkStarted = new Date().getTime();
            for (Client client : connectionHandler.getClients()) {
                if (new Date().getTime() - client.lastPing > 10000 + timeout_time_since_request) {
                    connectionHandler.main.serverHandler.quit(client);
                } else if (new Date().getTime() - client.lastPing > 10000) {
                    client.send(connectionHandler.parser.ping(client));
                }
            }
            long now = new Date().getTime();
            if (now - checkStarted < 1000) {
                try {
                    Thread.sleep(1000 - (now - checkStarted));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
