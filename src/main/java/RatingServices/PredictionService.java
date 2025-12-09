package RatingServices;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Repository.Song;
import Repository.User;

public class PredictionService {

    private List<Song> validSongs = new ArrayList<>();
    private List<User> validUsers = new ArrayList<>();

    public PredictionService(List<Song> validSongs, List<User> validUsers) {
        this.validSongs = validSongs;
        this.validUsers = validUsers;
    }

    public Map<Song, Map<String, Double>> getCompleteNormalizedPredictedSongs() {
        Map<Song, Map<String, Double>> normalizedPredictedSongRatings = new HashMap<>();
        for (Map.Entry<Song, Map<String, Double>> mapElement : getCompletePredictedSongRatings().entrySet()) {
            Song song = mapElement.getKey();
            Map<String, Double> songRatings = mapElement.getValue();
            if (Calculations.calculatePopulationStandardDeviation(songRatings) != 0.0) {
                normalizedPredictedSongRatings.put(song, Calculations.normalizeRatingsToThemselves(songRatings));
                //songsToPartition.add(song);
            }
        }
        return normalizedPredictedSongRatings;
    }

    private Map<Song, Map<String, Double>> getCompletePredictedSongRatings() {
        Map<Song, Map<String, Double>> predictedRatings = new HashMap<>();
        for (Song song : validSongs) {
            predictedRatings.put(song, fillAllMissingRatings(song));
        }
        return predictedRatings;
    }

    private Map<String, Double> fillAllMissingRatings(Song song) {
        Map<String, Double> completedRatings = new HashMap<>(song.getUserRatings());
        String songName = song.getName();
        for (User user : validUsers) {
            if (user.getSongRatings().get(songName) == null) {
                Double prediction = fillMissingRating(song, user);
                completedRatings.put(user.getName(),prediction);
            }
        }
        return completedRatings;
    }

    private Double fillMissingRating (Song song, User targetUser) {
        Double prediction = predictSingleMissingRating(song, targetUser);
        if (prediction.equals(Double.NaN)) return Calculations.calculateWeightedAverage(song, targetUser);
        else return prediction;
    }

    public Double predictSingleMissingRating(Song song, User targetUser) {
        List<User> similarUsers = buildUserDistances(targetUser);
        for (User otherUser : similarUsers) {
            Double normalizedPrediction = otherUser.getNormalizedRatings().get(song.getName());
            if (normalizedPrediction != null) {
                double prediction = targetUser.denormalizeSingleRating(normalizedPrediction);
                if (prediction < 1.0) prediction = 1.0;
                if (prediction > 5.0) prediction = 5.0;
                return prediction;
            }
        }
        // Case: there is no other valid similar user who has rated that song.
        return Double.NaN;
    }

    public List<User> buildUserDistances(User targetUser) {
        Map<User, Double> userSimilarityMap = new HashMap<>();
        for (User otherUser : validUsers) {
            if (!otherUser.equals(targetUser)) {
                Double userDistance = Calculations.calculateEuclideanDistance(targetUser.getNormalizedRatings(), otherUser.getNormalizedRatings(), validSongs);
                if (!userDistance.equals(Double.NaN)) {
                    userSimilarityMap.put(otherUser, userDistance);
                }
            }
        }
        List<User> rankedUserSimilarity = new ArrayList<>(userSimilarityMap.keySet());
        rankedUserSimilarity.sort( (a, b) -> { return userSimilarityMap.get(a).compareTo(userSimilarityMap.get(b)); });
        return rankedUserSimilarity;
    }
    
}
