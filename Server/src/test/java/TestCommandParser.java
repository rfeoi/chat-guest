import nspirep2p.application.server.commandParser.Command;
import nspirep2p.application.server.commandParser.CommandParser;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This tests the Command Parser
 * Created by strifel on 17.12.2018.
 */
public class TestCommandParser {
    private CommandParser parser;

    @BeforeAll
    public void setup() {
        parser = new CommandParser(null, null);
    }

    @Test
    public void testNormalCommand() {
        parser.inserManually("stop");
        if (parser.getLastExecuted().isCorrect()) {
            assertEquals(Command.STOP, parser.getLastExecuted().getCommand());
        } else {
            fail("Command not marked as Correct");
        }
    }

    @Test
    public void testParentCommand() {
        parser.inserManually("exit");
        if (parser.getLastExecuted().isCorrect()) {
            assertEquals(Command.STOP, parser.getLastExecuted().getCommand());
        } else {
            fail("Command not marked as Correct");
        }
    }

    @Test
    public void testWrongCommand() {
        parser.inserManually("testAWrongCommand");
        assertEquals(false, parser.getLastExecuted().isCorrect());
    }


}
