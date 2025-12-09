package RatingServices;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Repository.Song;
import Repository.User;

public class NormalizingService {

    private List<Song> songs = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    public NormalizingService(List<Song> songs, List<User> users) {
        this.songs = songs;
        this.users = users;
    }
    
    public Map<Song, Map<String, Double>> buildNormalizedSongsWithRespectToUsers() {
        Map<String, Map<String, Double>> normalizedUsers = buildNormalizedUserRatings();
        Map<Song, Map<String, Double>> normalizedSongs = new HashMap<>();
        for (Song song : songs) {
            Map<String, Double> normalizedRatings = normalizeSongRatingsWithRespectToUsers(song, normalizedUsers);
            normalizedSongs.put(song, normalizedRatings);
        }
        return normalizedSongs;
    }

    private Map<String, Double> normalizeSongRatingsWithRespectToUsers(Song song, Map<String, Map<String, Double>> normalizedUsers) {
        Map<String, Double> ratings = song.getUserRatings();
        String songName = song.getName();
        Map<String, Double> normalizedRatings = new HashMap<>();
        for (Map.Entry<String, Double> mapElement : ratings.entrySet()) {
            String userName = mapElement.getKey();           
            double normalizedRating = normalizedUsers.get(userName).get(songName);
            normalizedRatings.put(userName, normalizedRating);
        }         
        return normalizedRatings;
    }

    private Map<String, Map<String, Double>> buildNormalizedUserRatings() {
        Map<String, Map<String, Double>> normalizedUsers = new HashMap<>();
        for (User user : users) {
            Map<String, Double> normalizedRatings = user.getNormalizedRatings();
            normalizedUsers.put(user.getName(), normalizedRatings);
        }
        return normalizedUsers;
    }
}
