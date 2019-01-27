import nspirep2p.application.server.commandParser.Command;
import org.junit.jupiter.api.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;


/**
 * Checks the commands
 * Created by strifel on 17.12.2018.
 */
public class TestCommands {

    /**
     * This test checks if the right amount of parameters are given
     */
    @Test
    public void checkCommandParameters() {
        assertEquals(0, Command.BREAK.getMinParameter());
        assertEquals(0, Command.BREAK.getMaxParameter());
        assertEquals(0, Command.STOP.getMinParameter());
        assertEquals(0, Command.STOP.getMaxParameter());
        assertEquals(0, Command.CLOSE.getMinParameter());
        assertEquals(0, Command.CLOSE.getMaxParameter());
        assertEquals(0, Command.EXIT.getMinParameter());
        assertEquals(0, Command.EXIT.getMaxParameter());
        assertEquals(2, Command.CCHANNEL.getMaxParameter());
        assertEquals(2, Command.CCHANNEL.getMinParameter());
        assertEquals(0, Command.HELP.getMinParameter());
        assertEquals(1, Command.HELP.getMaxParameter());
        assertEquals(1, Command.KICK.getMinParameter());
        assertEquals(2, Command.KICK.getMaxParameter());
        assertEquals(0, Command.LISTCLIENTS.getMinParameter());
        assertEquals(0, Command.LISTCLIENTS.getMaxParameter());
        assertEquals(0, Command.LIST.getMinParameter());
        assertEquals(0, Command.LIST.getMaxParameter());
        assertEquals(0, Command.KICKALL.getMinParameter());
        assertEquals(1, Command.KICKALL.getMaxParameter());
        assertEquals(2, Command.CHANGEUSERNAME.getMinParameter());
        assertEquals(2, Command.CHANGEUSERNAME.getMaxParameter());
        assertEquals(2, Command.USERNAME.getMinParameter());
        assertEquals(2, Command.USERNAME.getMaxParameter());
        assertEquals(1, Command.ALERTALL.getMinParameter());
        assertEquals(1, Command.ALERTALL.getMaxParameter());
        assertEquals(2, Command.RENAMECHANNEL.getMinParameter());
        assertEquals(2, Command.RENAMECHANNEL.getMaxParameter());
        assertEquals(2, Command.RCHANNEL.getMinParameter());
        assertEquals(2, Command.RCHANNEL.getMaxParameter());
        assertEquals(1, Command.SHOWUSERNAME.getMinParameter());
        assertEquals(1, Command.SHOWUSERNAME.getMaxParameter());
        assertEquals(1, Command.SHOWUUID.getMinParameter());
        assertEquals(1, Command.SHOWUUID.getMaxParameter());
        assertEquals(1, Command.BAN.getMinParameter());
        assertEquals(2, Command.BAN.getMaxParameter());
        assertEquals(1, Command.UNBAN.getMinParameter());
        assertEquals(1, Command.UNBAN.getMaxParameter());
        assertEquals(2, Command.ADDPERM.getMinParameter());
        assertEquals(2, Command.ADDPERM.getMaxParameter());
        assertEquals(2, Command.REMOVEPERM.getMinParameter());
        assertEquals(2, Command.REMOVEPERM.getMaxParameter());
    }

    /**
     * This checks if the parent is right
     */
    @Test
    public void checkCommandParents() {
        assertEquals(Command.STOP, Command.BREAK.getParent());
        assertEquals(Command.STOP, Command.CLOSE.getParent());
        assertEquals(Command.STOP, Command.EXIT.getParent());
        assertEquals(Command.LISTCLIENTS, Command.LIST.getParent());
        assertEquals(Command.CHANGEUSERNAME, Command.USERNAME.getParent());
        assertEquals(Command.RENAMECHANNEL, Command.RCHANNEL.getParent());
        assertNull(Command.STOP.getParent());
        assertNull(Command.CCHANNEL.getParent());
        assertNull(Command.LISTCLIENTS.getParent());
        assertNull(Command.HELP.getParent());
        assertNull(Command.KICK.getParent());
        assertNull(Command.KICKALL.getParent());
        assertNull(Command.USERNAME.getParent());
        assertNull(Command.ALERTALL.getParent());
        assertNull(Command.RENAMECHANNEL.getParent());
        assertNull(Command.SHOWUSERNAME.getParent());
        assertNull(Command.SHOWUUID.getParent());
        assertNull(Command.BAN.getParent());
        assertNull(Command.UNBAN.getParent());
        assertNull(Command.ADDPERM.getParent());
        assertNull(Command.REMOVEPERM.getParent());
    }

}
