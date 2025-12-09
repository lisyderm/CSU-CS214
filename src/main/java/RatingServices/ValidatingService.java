package RatingServices;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Repository.Song;
import Repository.User;

public class ValidatingService {

    private List<Song> songs = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    public ValidatingService(List<Song> songs, List<User> users) {
        this.songs = songs;
        this.users = users;
    }

    public ValidatingService(List<User> users) {
        this.users = users;
    }

    public List<Song> buildValidSongList() {
        List<Song> validSongs = new ArrayList<>();
        for (Song song : songs) {
            if (checkSongIsValid(song)) {
                validSongs.add(song);
            }
        }
        return validSongs;
    }

    private boolean checkSongIsValid(Song song) {
        //for-each loop for Map code inspired by https://www.geeksforgeeks.org/java/traverse-through-a-hashmap-in-java/
        Map<String, Double> userRatings = song.getUserRatings();
        if (userRatings.size() < 1) {
            return false;
        }
        for (Map.Entry<String, Double> mapElement : userRatings.entrySet()) {
            if (checkUserIsValid(mapElement.getKey())) {
                return true;
            }
        }
        return false; 
    }

    private boolean checkUserIsValid(String userToCheck) {
        for (User user : users) {
            if (user.getName().equals(userToCheck) && user.getRatingVariation() > 0) {
                return true;
            }
        }
        return false;
    }

    public List<User> buildValidUserList() {
        ArrayList<User> validUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getRatingVariation() > 0) {
                validUsers.add(user);
            }
        }
        return validUsers;
    }
}
