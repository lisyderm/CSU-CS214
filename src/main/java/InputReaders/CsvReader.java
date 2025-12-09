package InputReaders;
import java.io.File;
import java.io.FileNotFoundException;

import java.util.Scanner;

import Repository.RatingRepository;

public class CsvReader {

private final String inputFile;
private RatingRepository ratingRepository;

public CsvReader(String inputFile) {
        this.inputFile = inputFile;
        ratingRepository = new RatingRepository();
        readInputFile();
    }

private void readInputFile() {
        File file = new File(inputFile);
        checkInputFileIsValid(file);
        try (Scanner lineReader = new Scanner(file);) {
            String songLine;
            String songName;
            String user;
            double rating;
            while (lineReader.hasNext()) {
                songLine = lineReader.nextLine();
                String[] parts = songLine.split(",");
                if (parts.length != 3) {
                    lineReader.close();
                    throw new IllegalArgumentException("invalid number of componenets on CSV line");
                }
                songName = parts[0];
                user = parts[1];
                rating = ratingCheck(parts[2]); //throws NumberFormatException for notAnInt or not 1-5
                ratingRepository.addNewRating(songName, user, rating);
            }
        }
        catch (FileNotFoundException e) {
            System.err.printf("Error: %s\n", e.getMessage());
        }
    }

    private void checkInputFileIsValid(File file) {
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("Input file does not exist or is not a file");
        }
        if (file.length() == 0) {
            // change exception type?
            throw new IllegalArgumentException("Input file is empty");
        }
    }

    private double ratingCheck(String ratingToCheck) {
        int integerRating = Integer.parseInt(ratingToCheck.strip());
        if (integerRating > 0 && integerRating < 6) {
            return Double.parseDouble(ratingToCheck.strip());
        }
        else throw new NumberFormatException("rating in csv input (" + ratingToCheck + ") is not between 1 and 5");
    }
    
    public RatingRepository getRatingRepository() {
        return ratingRepository;
    }
}
