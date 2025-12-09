package KMeansClustering;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Repository.Song;

public class Centroid implements Comparable<Centroid> {

    private Map<String, Double> updateableRatings = new HashMap<>();
    private final String initialSelection;

    public Centroid(String initialSelection, Map<String, Double> ratings) {
        updateableRatings = ratings;
        this.initialSelection = initialSelection;
    }

    public void recalculateCentroid(List<Song> songPartition, Map<Song, Map<String, Double>> normalizedPredictedSongRatings) {
        Map<String, Double> recalculatedRatings = new HashMap<>(updateableRatings);
        for (String userName : recalculatedRatings.keySet()) {
            Double newMeanPartitionRating = 0.0;
            for (Song song : songPartition) {
                Map<String, Double> ratings = normalizedPredictedSongRatings.get(song);
                newMeanPartitionRating += ratings.get(userName);
            }
            newMeanPartitionRating /= songPartition.size();
            recalculatedRatings.put(userName, newMeanPartitionRating);
        }
        updateableRatings = recalculatedRatings;
    }

    public Map<String, Double> getRatingsMap() {
        return updateableRatings;
    }

    public String getInitialSelection() {
        return initialSelection;
    }

    public int compareTo(Centroid otherCentroid) {
        return this.initialSelection.compareTo(otherCentroid.getInitialSelection());
    }
    
}
