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
        assertEquals(2, Command.CCHANEL.getMaxParameter());
        assertEquals(2, Command.CCHANEL.getMinParameter());
        assertEquals(0, Command.HELP.getMinParameter());
        assertEquals(1, Command.HELP.getMaxParameter());
        assertEquals(1, Command.KICK.getMinParameter());
        assertEquals(2, Command.KICK.getMaxParameter());
        assertEquals(0, Command.LISTCLIENTS.getMinParameter());
        assertEquals(0, Command.LISTCLIENTS.getMaxParameter());
        assertEquals(0, Command.LIST.getMinParameter());
        assertEquals(0, Command.LIST.getMaxParameter());
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
        assertNull(Command.STOP.getParent());
        assertNull(Command.CCHANEL.getParent());
        assertNull(Command.LISTCLIENTS.getParent());
        assertNull(Command.HELP.getParent());
        assertNull(Command.KICK.getParent());
    }

}
