import nspirep2p.communication.protocol.Client;
import nspirep2p.communication.protocol.ClientType;
import nspirep2p.communication.protocol.v1.CommunicationParser;
import nspirep2p.communication.protocol.v1.Function;
import nspirep2p.communication.protocol.v1.Package;
import nspirep2p.communication.protocol.v1.WrongPackageFormatException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


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
        assertNotNull(cPackage.getArg(Function.INVITE.getParameters()[0]));
        assertEquals(cPackage.getArg(Function.INVITE.getParameters()[0]), client1.username);

    }
    @Test
    public void sendMessage() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        client.uuid = "1234";
        String[] sendMessage = cparser.sendMessage(client,"1234567890","Hallo");
        Package cPackage = parser.parsePackage(sendMessage);
        assertEquals(cPackage.getAuthUUID(), client.uuid);
        assertEquals(cPackage.getFunction(), Function.SEND_MESSAGE);
        assertEquals(cPackage.getArg(Function.SEND_MESSAGE.getParameters()[0]), "1234567890");
        assertEquals(cPackage.getArg(Function.SEND_MESSAGE.getParameters()[1]), "Hallo");

    }
    @Test
    public void sendError() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        String[] sendError = parser.sendError("NullPointer");
        Package cPackage = cparser.parsePackage(sendError);
        assertEquals(cPackage.getFunction(),Function.SEND_ERROR);
        assertEquals(cPackage.getArg(Function.SEND_ERROR.getParameters()[0]),"NullPointer");

    }
    @Test
    public void getClients() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        Client[] clients = new Client[10];
        String usernames = "";
        String uuid = "";
        for(int i = 0;i<=9;i++){
            clients[i] = new Client();
            clients[i].username = i+"";
            clients[i].uuid = i+"";
            usernames += clients[i].username+",";
            uuid += clients[i].uuid+",";
        }
        usernames = usernames.substring(0, usernames.length() -1);
        uuid = uuid.substring(0, uuid.length() -1);
        String[] getClients = parser.getClients(client,clients,true);
        Package cPackage = cparser.parsePackage(getClients);
        assertEquals(cPackage.getFunction(),Function.GET_CLIENTS);
        assertEquals(cPackage.getArg(Function.GET_CLIENTS.getParameters()[0]),"0,1,2,3,4,5,6,7,8,9");
        assertEquals(cPackage.getArg(Function.GET_CLIENTS.getParameters()[1]),"0,1,2,3,4,5,6,7,8,9");

    }
    @Test
    public void getChannels() throws WrongPackageFormatException {
        CommunicationParser cparser = new CommunicationParser(ClientType.CLIENT);
        CommunicationParser parser = new CommunicationParser(ClientType.SERVER);
        Client client = new Client();
        String[] strings = {"1","2","3","4","5"};
        String[] getChannels = parser.getChannels(client,strings);
        Package cPackage = cparser.parsePackage(getChannels);
        assertEquals(cPackage.getFunction(),Function.GET_CHANNELS);
        assertEquals(cPackage.getArg(Function.GET_CHANNELS.getParameters()[0]),"1,2,3,4,5");

    }
    //TODO write test for kick

}
