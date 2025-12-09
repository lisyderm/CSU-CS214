package OutputGenerators.OutputTypes;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import RatingServices.Calculations;
import RatingServices.RatingService;
import Repository.Song;
import Repository.User;

public class SongSimilarity implements Printable {

    private final List<Song> validSongs;
    private final List<User> validUsers;
    private final Map<Song, Map<String, Double>> normalizedSongs; 

    public SongSimilarity(RatingService ratingService) {
        validSongs = ratingService.getValidSongList();
        validUsers = ratingService.getValidUserList();
        normalizedSongs = ratingService.getNormalizedSongsWithRespectToUsers();
    }

    @Override
    public String toString() {
        return buildSongPairOutput();
    }

    private String buildSongPairOutput() {
        if (validUsers.size() > 1) {
            String output = "name1,name2,similarity\n";
            Collections.sort(validSongs);
            for (int i = 0; i < validSongs.size(); i++) {
                for (int j = i + 1; j < validSongs.size(); j++) {
                Song song1 = validSongs.get(i);
                Song song2 = validSongs.get(j);
                double distance = Calculations.calculateEuclideanDistance(normalizedSongs.get(song1), normalizedSongs.get(song2), validUsers);
                output += song1.getName() + "," + song2.getName() + "," + distance + "\n";
                }
            }
            return output;
        }
        else {
            throw new IllegalArgumentException("at least two cooperative users are required for song similarity output");
        }
    }
}