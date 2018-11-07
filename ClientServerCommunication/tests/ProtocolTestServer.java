import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nspirep2p.communication.protocol.Client;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.CommunicationParser;
import org.junit.jupiter.api.Test;

import java.util.Random;

/**
 * Tests the parser class used for communication between client and server
 * Created by strifel on 07.11.2018.
 */
public class ProtocolTestServer {
    @Test
    public void changeUsername(){
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.uuid = new Random().nextInt(Integer.MAX_VALUE) + "";
        String[] response = parser.pushUsername(client, "test");
        assertEquals(3, response.length, "Response from changeUsername is not 3 long");
        assertEquals("client.uuid=" + client.uuid, response[0]);
        assertEquals("client.username=test", response[1]);
        assertEquals("break", response[2]);
    }

    @Test
    public void checkHandshakeParsing(){
        CommunicationParser parser = new CommunicationParser(ClientType.CLIENT);
        Client client = new Client();
        String[] handshake = parser.doHandshake(client);
        assertNotNull(handshake, "Oh something went wrong in checkHandshakeParsing in Server Class. Error on client side");
        parser = new CommunicationParser(ClientType.SERVER);
        Client newClient = new Client();
        String[] acceptHandshake = parser.parseClientHandshake(handshake, newClient);
        assertEquals(client.uuid, newClient.username);
        assertEquals(2, acceptHandshake.length);
        assertEquals("accept", acceptHandshake[0]);
        assertEquals("waiting", acceptHandshake[1], "Server does not wait for client");
    }
}
