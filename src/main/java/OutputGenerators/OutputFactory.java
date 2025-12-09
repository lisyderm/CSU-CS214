package OutputGenerators;
import java.util.List;

import OutputGenerators.OutputTypes.Printable;
import OutputGenerators.OutputTypes.PersonalRecommendations;
import OutputGenerators.OutputTypes.SongAnalysis;
import OutputGenerators.OutputTypes.SongRecommendations;
import OutputGenerators.OutputTypes.SongSimilarity;
import OutputGenerators.OutputTypes.UserAnalysis;
import OutputGenerators.OutputTypes.UserPredictions;
import RatingServices.RatingService;

public class OutputFactory {
    
    private String outputType;
    private RatingService ratingService;
    private List<String> userSelections;

    public OutputFactory (String[] outputInfo, RatingService ratingService, List<String> userSelections) {
        this.userSelections = userSelections;
        outputType = outputInfo[1];
        this.ratingService = ratingService;
        String textToPrint = produceOutputOfCorrectType().toString();
        new PrintPresenter(textToPrint, outputInfo[0]);
    }

    private Printable produceOutputOfCorrectType() {
        return switch(outputType) {
            case "-a" -> new UserAnalysis(ratingService);
            case "-u" -> new SongSimilarity(ratingService);
            case "-p" -> new UserPredictions(ratingService);
            case "-r" -> new SongRecommendations(ratingService, userSelections);
            case "-s" -> new PersonalRecommendations(ratingService, userSelections);
            default -> new SongAnalysis(ratingService);
        };
    }
}
