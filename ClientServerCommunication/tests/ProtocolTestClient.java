import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import nspirep2p.communication.protocol.Client;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.v1.CommunicationParser;
import nspirep2p.communication.protocol.v1.Function;
import nspirep2p.communication.protocol.v1.Package;
import nspirep2p.communication.protocol.v1.WrongPackageFormatException;
import org.junit.jupiter.api.Test;

/**
 * A Test class
 * Created by robmroi03 on 26.11.2018.
 */
public class ProtocolTestClient {

    @Test
    public void parseUsernameChange() throws WrongPackageFormatException {
        CommunicationParser snsparser = new CommunicationParser(ClientType.SERVER);
        CommunicationParser parser = new CommunicationParser(ClientType.CLIENT);
        Client client = new Client();
        client.username = "aUsername";
        String[] changeUsernameParse = snsparser.pushUsername(client, "aNewUsername");
        Package sPackage = parser.parsePackage(changeUsernameParse);
        assertNotNull(sPackage);
        assertNotNull(sPackage.getArg(Function.CHANGE_USERNAME.getParameters()[0]));
        assertNotNull(sPackage.getArg(Function.CHANGE_USERNAME.getParameters()[1]));
        assertEquals("aUsername", sPackage.getArg(Function.CHANGE_USERNAME.getParameters()[0]));
        assertEquals("aNewUsername", sPackage.getArg(Function.CHANGE_USERNAME.getParameters()[1]));

    }
    @Test
    public void sendMessage() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.uuid = "1234567890";
        String[] sendMessage = parser.sendMessage(client,"1234567890","Hallo");
        Package cPackage = cparser.parsePackage(sendMessage);
        assertEquals(cPackage.getAuthUUID(), client.uuid);
        assertEquals(cPackage.getFunction(), Function.SEND_MESSAGE);
        assertEquals(cPackage.getArg(Function.SEND_MESSAGE.getParameters()[0]), "1234567890");
        assertEquals(cPackage.getArg(Function.SEND_MESSAGE.getParameters()[1]), "Hallo");

    }
    @Test
    public void changeUsername() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.username = "test1";
        String[] response = parser.pushUsername(client, "test");
        Package responsePackage = cparser.parsePackage(response);
        assertEquals(4, response.length, "Response from changeUsername is not 3  long");
        assertEquals(Function.CHANGE_USERNAME, responsePackage.getFunction());
        assertEquals("test1", responsePackage.getArg(Function.CHANGE_USERNAME.getParameters()[0]));
        assertEquals("test", responsePackage.getArg(Function.CHANGE_USERNAME.getParameters()[1]));
    }
    @Test
    public void parseMove() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.username = "test1";
        String[] moveClient = parser.moveClient(client, "test");
        Package responsePackage = cparser.parsePackage(moveClient);
        assertEquals(responsePackage.getFunction(), Function.MOVE);
        assertEquals("test1",responsePackage.getArg(Function.MOVE.getParameters()[0]));
        assertEquals("test",responsePackage.getArg(Function.MOVE.getParameters()[1]));

    }
    @Test
    public void parseInvite() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        Client client1 = new Client();
        client.username = "test";
        String[] moveClient = parser.inviteClient(client, client1);
        Package responsePackage = cparser.parsePackage(moveClient);
        assertEquals(responsePackage.getFunction(), Function.INVITE);
        assertEquals("test",responsePackage.getArg(Function.INVITE.getParameters()[0]));

    }
    @Test
    public void parseCreateTempChannel() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.username = "test";
        String[] createTempChannel = parser.createTempChannel(client);
        Package responsePackage = cparser.parsePackage(createTempChannel);
        assertEquals(responsePackage.getFunction(), Function.CREATE_TEMP_CHANNEL);
        assertEquals("test",responsePackage.getArg(Function.CREATE_TEMP_CHANNEL.getParameters()[0]));

    }


}
