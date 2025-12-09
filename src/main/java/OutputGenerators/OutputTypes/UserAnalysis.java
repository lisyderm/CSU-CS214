package OutputGenerators.OutputTypes;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import RatingServices.RatingService;
import Repository.Song;
import Repository.User;

public class UserAnalysis implements Printable {

    private final List<Song> validSongs;
    private final List<User> validUsers;

    public UserAnalysis(RatingService ratingService) {
        validSongs = ratingService.getValidSongList();
        validUsers = ratingService.getValidUserList();
    }

    @Override
    public String toString() {
        return buildUserAnalysis();
    }
    
    private String buildUserAnalysis() {
        Collections.sort(validUsers);
        Collections.sort(validSongs);
        String outputToPrint = "username,song,rating\n";
        for (User validUser : validUsers) {
            String userName = validUser.getName();
            Map<String, Double> userRatings = validUser.getSongRatings();
            outputToPrint += buildSingleUserLines(userRatings, userName);
        }
        return outputToPrint;
    }

    private String buildSingleUserLines(Map<String, Double> userRatings, String userName) {
        String output = "";
        for (Song song : validSongs) {
            String songName = song.getName();
            Double userRating = userRatings.get(songName);
            if (userRating == null) {
                userRating = Double.NaN;
            }
            output += String.format("%s,%s,%.0f\n", userName, songName, userRating);
        }
        return output;
    }
}
