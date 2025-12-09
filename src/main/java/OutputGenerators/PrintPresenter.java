package OutputGenerators;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class PrintPresenter {

        public PrintPresenter(String textToPrint, String outputFile) {
        try {
            PrintWriter printWriter = new PrintWriter(new FileWriter(outputFile));
            printWriter.print(textToPrint);
            printWriter.close();
        }
        catch (IOException e) {
            System.err.printf("Error: %s\n", e.getMessage());
        }
    }
}
