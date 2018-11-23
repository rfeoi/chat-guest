import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import nspirep2p.communication.protocol.*;
import org.junit.jupiter.api.Test;


/**
 * Tests the parser class used for communication between client and server
 * Created by strifel on 07.11.2018.
 */
public class ProtocolTestServer {
    @Test
    public void changeUsername(){
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.username = "test1";
        String[] response = parser.pushUsername(client, "test");
        assertEquals(4, response.length, "Response from changeUsername is not 3  long");
        assertEquals("function=CHANGE_USERNAME", response[0]);
        assertEquals("client.username=test1", response[1]);
        assertEquals("client.newUsername=test", response[2]);
        assertEquals("break", response[3]);
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
        assertEquals(client.uuid, newClient.uuid);
        assertEquals(2, acceptHandshake.length);
        assertEquals("accept", acceptHandshake[0]);
        assertEquals("waiting", acceptHandshake[1], "Server does not wait for client");
    }

    @Test
    public void parseUsernameChange() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.username = "test";
        client.uuid = "1234";
        String[] usernameChange = cparser.pushUsername(client, "myUsername");
        nspirep2p.communication.protocol.Package cPackage = parser.parsePackage(usernameChange);
        assertEquals(cPackage.getAuthUUID(), "1234");
        assertEquals(cPackage.getFunction(), Function.CHANGE_USERNAME);
        assertNotNull(cPackage.getArg("client.username"));
        assertEquals(cPackage.getArg("client.username"), "myUsername");

    }
}
