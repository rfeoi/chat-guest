import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import nspirep2p.communication.protocol.*;
import nspirep2p.communication.protocol.v1.CommunicationParser;
import nspirep2p.communication.protocol.v1.Function;
import nspirep2p.communication.protocol.v1.Package;
import nspirep2p.communication.protocol.v1.WrongPackageFormatException;
import org.junit.jupiter.api.Test;


/**
 * Tests the parser class used for communication between client and server
 * Created by strifel on 07.11.2018.
 */
public class ProtocolTestServer {

    //Test is no longer relevant (see parseUsernameChange)
    @Deprecated
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
        Package cPackage = parser.parsePackage(usernameChange);
        assertEquals(cPackage.getAuthUUID(), "1234");
        assertEquals(cPackage.getFunction(), Function.CHANGE_USERNAME);
        assertNotNull(cPackage.getArg("client.username"));
        assertEquals(cPackage.getArg("client.username"), "myUsername");
    }

    @Test
    public void parseCreateTempChannel() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.username = "test";
        client.uuid = "1234";
        String[] createTempChannel = cparser.createTempChannel(client);
        Package cPackage = parser.parsePackage(createTempChannel);
        assertEquals(cPackage.getAuthUUID(), client.uuid);
        assertEquals(cPackage.getFunction(), Function.CREATE_TEMP_CHANNEL);
    }
    @Test
    public void parseMove() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.username = "test";
        client.uuid = "1234";
        String[] moveClient = cparser.moveClient(client, "test");
        Package cPackage = parser.parsePackage(moveClient);
        assertEquals(cPackage.getAuthUUID(), client.uuid);
        assertEquals(cPackage.getFunction(), Function.MOVE);
        assertNull(cPackage.getArg(Function.MOVE.getParameters()[0]));
        assertNotNull(cPackage.getArg(Function.MOVE.getParameters()[1]));
        assertEquals(cPackage.getArg(Function.MOVE.getParameters()[1]), "test");
    }

    @Test
    public void parseInvite() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        Client client1 = new Client();
        client1.username = "test";
        client.uuid = "1234";
        String[] moveClient = cparser.inviteClient(client, client1);
        Package cPackage = parser.parsePackage(moveClient);
        assertEquals(cPackage.getAuthUUID(), client.uuid);
        assertEquals(cPackage.getFunction(), Function.INVITE);
        assertNotNull(cPackage.getArg(Function.MOVE.getParameters()[0]));
        assertEquals(cPackage.getArg(Function.MOVE.getParameters()[0]), client1.username);
    }


}