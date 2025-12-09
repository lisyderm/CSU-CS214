package Repository;
import java.util.ArrayList;
import java.util.List;

/** Holds all ratings as a list of users and a list of songs. */
public class RatingRepository {

    private List<Song> songs = new ArrayList<>();
    private List<User> users = new ArrayList<>();

    /**
     * Adds a new rating by either adding it to an existing song and user
     * or creating a new song and/or user as needed.
     * @param songName The name of the song.
     * @param userName The name of the user.
     * @param rating The integer rating between 1-5 stored as a double.
     */
    public void addNewRating(String songName, String userName, double rating) {
        if (!addRatingToExistingSong(songName, userName, rating)) {
            createNewSong(songName, userName, rating);
        }
        if (!addRatingtoExistingUser(userName, songName, rating)) {
            createNewUser(userName, songName, rating);
        }
    }

    private boolean addRatingToExistingSong(String songName, String user, double rating) {
        for (Song song: songs) {
            if (song.getName().equals(songName)) {
                song.addNewRating(user, rating);
                return true;
            }
        }
        return false;
    }
    
    private void createNewSong(String songName, String user, double rating) {
        Song newSong = new Song(songName, user, rating);
        songs.add(newSong);
    }

    private boolean addRatingtoExistingUser(String userName, String song, double rating) {
        for (User user : users) {
            if (user.getName().equals(userName)) {
                user.addNewRating(song, rating);
                return true;
            }
        }
        return false;
    }

    private void createNewUser(String user, String songName, double rating) {
        User newUser = new User(user, songName, rating);
        users.add(newUser);
    }

    /**
     * Get a list of all songs in the repository.
     * @return A list of all song objects.
     */
    public List<Song> getSongs() {
        return new ArrayList<Song>(songs);
    }

    /**
     * Get a list of all users in the repository.
     * @return A list of all user objects.
     */
    public List<User> getUsers() {
        return new ArrayList<User>(users);
    }
}
