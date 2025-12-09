package InputReaders;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import OutputGenerators.OutputFactory;
import RatingServices.RatingService;
import Repository.RatingRepository;

public class ArgParserInitializer {

    private String inputFile;
    private String outputFile;
    private String outputType = "default";
    private final String[] args;

    public ArgParserInitializer (String[] args) {
        this.args = args;
        checkAndLoadArgs();
        new OutputFactory(getOutputInfo(), getRatingService(), getUserSelections());
    }

    private String[] getOutputInfo() {
        checkOutputDirectoryExists();
        String[] outputInfo = {outputFile, outputType};
        return outputInfo;
    }

    private RatingService getRatingService() {
        CsvReader userRatingsData = new CsvReader(inputFile);
        RatingRepository ratingRepository = userRatingsData.getRatingRepository();
        return new RatingService(ratingRepository);
    }

    private List<String> getUserSelections() {
        List<String> userSelections = new ArrayList<>();

        if (outputType.equals("-r") || outputType.equals("-s")) {
            for (int i = 3; i < args.length; i++) {
                userSelections.add(args[i]);
            }
        }
        if (outputType.equals("-r")) {
            duplicateSelectionCheck(userSelections);
        }
        // for personal recommendations, skip checking centroid number
        if (outputType.equals("-s")) {
            duplicateSelectionCheck(userSelections.subList(1,userSelections.size()));
        }
        return userSelections;
    }

    private void duplicateSelectionCheck(List<String> selectionsToCheck) {
        Set<String> seenStrings = new HashSet<>();
        for (String selection : selectionsToCheck) {
            if (!seenStrings.add(selection)) {
                throw new IllegalArgumentException("duplicate user selection");
            }
        }
    }

    private void checkAndLoadArgs() {
        if (args.length < 2) {
            throw new IllegalArgumentException("too few arguments");
        }
        inputFile = args[0];
        outputFile = args[1];
        checkInputAndOutputAreCSVs();

        if (args.length > 2) {
            checkAcceptableNumberOfArgs(args);
            checkThirdArgument(args[2]);
            outputType = args[2];
        }
    }

    private void checkAcceptableNumberOfArgs(String[] args) {
        if (args[2].equals("-r")) {
            if (args.length < 4) {
            throw new IllegalArgumentException("must select at least one song for recommendations");
            }
            else return;
        }
        if (args[2].equals("-s")) {
            if (args.length < 5) {
            throw new IllegalArgumentException("must select at least one song for recommendations");
            }
            else return;
        }
        if (args.length > 3) {
            throw new IllegalArgumentException("too many arguments provided for output type");
        }
    }

    private void checkInputAndOutputAreCSVs() {
        if (inputFile.length() < 4 || outputFile.length() < 4) {
            throw new IllegalArgumentException("input or output file name is too short to be a CSV");
        }
        if (!inputFile.substring(inputFile.length()-4, inputFile.length()).equals(".csv") 
            || !outputFile.substring(outputFile.length()-4, outputFile.length()).equals(".csv")) {
            throw new IllegalArgumentException("input or output file is not a CSV");
        }
    }

    private void checkThirdArgument(String thirdArg) {
        if (thirdArg != null && !(thirdArg.equals("-a") || thirdArg.equals("-u") 
        || thirdArg.equals("-p") || thirdArg.equals("-r") || thirdArg.equals("-s"))) {
            throw new IllegalArgumentException("illegal third argument");
        }
    }

    private void checkOutputDirectoryExists() {
        File directory = new File(outputFile).getParentFile();
        if (directory != null && !directory.exists()) {
            throw new IllegalArgumentException("output file directory does not exist");
        }
    }
    
}
