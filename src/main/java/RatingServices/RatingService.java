package RatingServices;
import java.util.List;
import java.util.Map;

import Repository.RatingRepository;
import Repository.Song;
import Repository.User;

public class RatingService {
    
    private List<Song> songs;
    private List<User> users;
    private List<Song> validSongs;
    private List<User> validUsers;

    public RatingService(RatingRepository ratingRepository) {
        songs = ratingRepository.getSongs();
        users = ratingRepository.getUsers();
        ValidatingService validatingService = new ValidatingService(songs, users);
        validSongs = validatingService.buildValidSongList();
        validUsers = validatingService.buildValidUserList();

    }

    public User findUserByName(String userName) {
        for (User user : users) {
            if (user.getName().equals(userName)) {
                return user;
            }
        }
        throw new IllegalArgumentException("User not found in user list"); 
    }

    public Song findSongByName(String songName) {
        for (Song song : songs) {
            if (song.getName().equals(songName)) {
                return song;
            }
        }
        throw new IllegalArgumentException("Song not found in song list"); 
    }

    public Map<Song, Map<String, Double>> getNormalizedSongsWithRespectToUsers() {
        NormalizingService normalizingService = new NormalizingService(songs, users);
        return normalizingService.buildNormalizedSongsWithRespectToUsers();
    }

    // public PartitionBuilder getPartitionBuilder(List<String> centroidSeeds, List<Centroid> centroids, List<Song> songsToPartition) {
    //     return new PartitionBuilder(centroidSeeds, centroids, songsToPartition);
    // }

    public PredictionService getPredictionService() {
        return new PredictionService(validSongs, validUsers);
    }

    public List<Song> getValidSongList() {
        return validSongs;
    }

    public List<User> getValidUserList() {
        return validUsers;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public List<User> getUsers() {
        return users;
    }
}
