import org.apache.commons.math4.legacy.exception.NullArgumentException;

import InputReaders.ArgParserInitializer;

public class Cs214Project {

    public static void main(String[] args) {
        try {
            new ArgParserInitializer(args);
        }
        catch (IllegalArgumentException | NullArgumentException e) {
            System.err.println("Error: " + e.getMessage());
            return; 
        }
    }
}