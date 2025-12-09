package Repository;
import java.util.HashMap;
import java.util.Map;

import RatingServices.Calculations;

/**
 * Stores the user's name, a map of all ratings given that pairs song name keys 
 * to integer 1-5 ratings, and whether the user has any rating variation and is thus valid.
 */
public class User implements Comparable<User>, SimilarityComparable {

    private String userName;
    private Map<String, Double> songRatings = new HashMap<>();
    private int ratingVariation;
    private double firstRating;

    /**
     * Builds a new user from that user's initial rating.
     * @param userName The user's name.
     * @param firstSongRated The name of the song rated.
     * @param firstRating The integer rating 1-5.
     */
    public User(String userName, String firstSongRated, double firstRating) {
        this.userName = userName;
        songRatings.put(firstSongRated, firstRating);
        this.firstRating = firstRating;
        ratingVariation = 0;
    }

    /**
     * If the user is already in the repository, subsequent ratings 
     * are added to that user's ratings map.
     * @param song The name of the song rated.
     * @param rating The integer rating 1-5.
     */
    public void addNewRating(String song, double rating) {
        songRatings.put(song, rating);
        if (Math.abs(rating - firstRating) > 0.1) {
            ratingVariation++;
        }
    }

    /**
     * Uses the static utility class to covert the user's ratings map to normalized ratings,
     * normalized for that user's overall mean and standard deviation.
     * @return A map of normalized ratings pairing each song name to the new normalized rating as a double.
     */
    public Map<String, Double> getNormalizedRatings() {
        return Calculations.normalizeRatingsToThemselves(songRatings);
    }

    /**
     * Takes a normalized song rating and denormalizes it relative to this user.
     * @param normalizedPrediction The predicted rating that has been normalized relative to any user or song.
     * @return The predicted rating denormalized for this user's ratings map.
     */
    public double denormalizeSingleRating(double normalizedPrediction) {
        return Math.round(normalizedPrediction
         * Calculations.calculatePopulationStandardDeviation(songRatings)
         + Calculations.calculateAverageRating(songRatings));
    }

    @Override
    public int compareTo(User otherUser) {
        return this.userName.compareTo(otherUser.getName());
    }

    @Override
    public String getName() {
        return userName;
    }

    /**
     * Non-zero variation indicates a valid/cooperative user.
     * Currently the count of the user's songs that differ from the initial rating. Could be 
     * coverted to return a boolean.
     * @return The user's rating variation quantified as number of songs differing from the initial rating.
     */
    public int getRatingVariation() {
        return ratingVariation;
    }

    /**
     * Return the complete song ratings map, with song names as keys and
     * ratings as 1-5 integers.
     * @return The complete ratings map.
     */
    public Map<String, Double> getSongRatings() {
        return new HashMap<>(songRatings);
    }
}
