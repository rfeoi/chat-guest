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
        //MAX IST MEEEEEEEEEEEEEEEEGGGGGGGGGGAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA COOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOOL
        //Robin ist nicht ganz so cool wie Max
    }
}
