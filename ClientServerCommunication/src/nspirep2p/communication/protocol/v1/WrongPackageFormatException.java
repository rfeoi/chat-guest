package nspirep2p.communication.protocol.v1;

/**
 * Exception thrown when package has wrong format
 * Created by strifel on 12.11.2018.
 */
public class WrongPackageFormatException extends Exception {

    public WrongPackageFormatException(String line, String error) {
        super("Error with package format: " + error + " in line: " + line);
    }
}
