package Repository;
import java.util.Map;
import java.util.HashMap;

/** 
 * A song object stores the song name, a map of all user supplied ratings, and for 
 * kmeans clustering the distance from nearest user selection.
 */
public class Song implements Comparable<Song>, SimilarityComparable {
    private String songName;
    private Map<String, Double> userRatings = new HashMap<>();
    private Double distanceFromNearestSelection;

    /**
     * Builds a song from an initial rating.
     * @param songName The song name.
     * @param userName The user giving the rating.
     * @param rating The integer rating 1-5.
     */
    public Song(String songName, String userName, double rating) {
        this.songName = songName;
        userRatings.put(userName, rating); 
    }

    /**
     * If the song is already in the repository, a new rating is added to that song object.
     * @param userName The user giving the rating.
     * @param rating The integer rating 1-5.
     */
    public void addNewRating(String userName, double rating) {
        userRatings.put(userName, rating);
    }

    /**
     * If it is the first distance from selection, it initializes.
     * Otherwises it updates only if the new distance is smaller.
     * @param distance
     */
    public void setDistanceFromNearestSelection(double distance) {
        if (distanceFromNearestSelection == null) distanceFromNearestSelection = distance;
        if (distance > distanceFromNearestSelection) distanceFromNearestSelection = distance;
    }

    /**
     * The distance from the nearest user selection in the same partition.
     * @return Distance from nearest user selected song in the same partition after kmeans clustering.
     */
    public Double getDistanceFromNearest() {
        return distanceFromNearestSelection;
    }

    @Override
    public int compareTo(Song otherSong) {
        return this.songName.compareTo(otherSong.getName());
    }

    @Override
    public String getName() {
        return songName;
    }

    /**
     * Gets the map of all user supplied ratings. (Including invalid users.)
     * @return The map of userNames and the integer 1-5 rating given by each.
     */
    public Map<String, Double> getUserRatings() {
        return new HashMap<>(userRatings);
    }
}