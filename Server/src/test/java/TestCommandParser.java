import nspirep2p.application.server.commandParser.Command;
import nspirep2p.application.server.commandParser.CommandParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This tests the Command Parser
 * Created by strifel on 17.12.2018.
 */
public class TestCommandParser {
    private static CommandParser parser;

    @BeforeAll
    public static void setup() {
        parser = new CommandParser();
    }

    @Test
    public void testNormalCommand() {
        parser.insertManually("stop");
        if (parser.wasLastCorrect()) {
            assertEquals(Command.STOP, parser.getLastExecuted().getCommand());
        } else {
            fail("Command not marked as Correct");
        }
    }

    @Test
    public void testParentCommand() {
        parser.insertManually("close");
        if (parser.wasLastCorrect()) {
            assertEquals(Command.STOP, parser.getLastExecuted().getCommand());
        } else {
            fail("Command not marked as Correct");
        }
    }

    @Test
    public void testWrongCommand() {
        parser.insertManually("testAWrongCommand");
        assertEquals(false, parser.getLastExecuted().isCorrect());
    }


}
