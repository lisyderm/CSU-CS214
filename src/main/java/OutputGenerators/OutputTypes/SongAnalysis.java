package OutputGenerators.OutputTypes;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import RatingServices.Calculations;
import RatingServices.RatingService;
import Repository.Song;

public class SongAnalysis implements Printable {

    private final List<Song> songs;

    public SongAnalysis(RatingService ratingService) {
        songs = ratingService.getSongs();
    }

    @Override
    public String toString() {
        return buildSongStats();
    }

    private String buildSongStats() {
        String outputToPrint = "song,number of ratings,mean,standard deviation\n";
        Collections.sort(songs);
        for (Song song: songs) {
            outputToPrint += buildOutputLine(song);
        }
        return outputToPrint;
    }

    private String buildOutputLine(Song song) {
        Map<String, Double> userRatings = song.getUserRatings();
        return song.getName() + "," + userRatings.size() + "," + Calculations.calculateAverageRating(userRatings) + "," 
        + Calculations.calculatePopulationStandardDeviation(userRatings) + "\n";
    }
}
