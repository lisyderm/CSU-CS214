package OutputGenerators.OutputTypes;
import java.util.Collections;
import java.util.List;

import RatingServices.RatingService;
import Repository.Song;
import Repository.User;

public class UserPredictions implements Printable {

    private final RatingService ratingService;
    private final List<Song> validSongs;
    private final List<User> validUsers;
    private int countRatingsPredicted = 0;

    public UserPredictions(RatingService ratingService) {
        this.ratingService = ratingService;
        validSongs = ratingService.getValidSongList();
        validUsers = ratingService.getValidUserList();
        Collections.sort(validSongs);
        Collections.sort(validUsers);
    }

    @Override
    public String toString() {
        if (validUsers.size() < 2) {
            throw new IllegalArgumentException("minimum two cooperative users required for predictions");
        }
        String output = "song,user,predicted rating\n";
        for (Song song : validSongs) {
            output += buildAllMissingRatings(song);
        }
        if (countRatingsPredicted == 0) {
            throw new IllegalArgumentException("no predictions to be made");
        }
        return output;
    }

    private String buildAllMissingRatings(Song song) {
        String songOutputLines = "";
        String songName = song.getName();
        for (User user : validUsers) {
            if (user.getSongRatings().get(songName) == null) {
                Double prediction = predictMissingRating(song, user);
                songOutputLines += String.format("%s,%s,%.0f\n",songName,user.getName(),prediction);
                countRatingsPredicted++;            
            }
        }
        return songOutputLines;
    }

    private Double predictMissingRating(Song song, User targetUser) {
        return ratingService.getPredictionService().predictSingleMissingRating(song, targetUser);
    }
}
