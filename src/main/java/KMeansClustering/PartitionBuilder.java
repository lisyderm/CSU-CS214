package KMeansClustering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import RatingServices.Calculations;
import Repository.Song;
import Repository.User;

public class PartitionBuilder {

    private Map<Song, Map<String, Double>> normalizedPredictedSongs;
    private List<User> validUsers;
    private List<Centroid> centroids;
    private Map<Centroid, List<Song>> updateablePartitionMap = new HashMap<>();

    public PartitionBuilder (List<Centroid> centroids, Map<Song, Map<String, Double>> normalizedPredictedSongs, List<User> validUsers) {
        this.centroids = centroids;
        this.normalizedPredictedSongs = normalizedPredictedSongs;
        this.validUsers = validUsers;
        if (normalizedPredictedSongs.size() < centroids.size() + 1) {
            throw new IllegalArgumentException("there are no songs to recommend. Songs may have been removed. Try with a larger file or fewer selections.");
        }
    }

    public Map<Centroid, List<Song>> buildPartitionMap() {
        updateablePartitionMap = assignSongsToCentroids();
        for (int i = 0; i < 10; i++) {
            reassignSongsToCentroids();
        }
        return updateablePartitionMap;
    }

    private Map<Centroid, List<Song>> assignSongsToCentroids() {
        Map<Centroid, List<Song>> centroidPartition = new HashMap<>();
        for (Centroid centroid : centroids) {
            List<Song> songsInPartition = new ArrayList<>();
            centroidPartition.put(centroid, songsInPartition);
        }
        for (Song song : normalizedPredictedSongs.keySet()) {
            Map<Centroid, Double> centroidSimilarityMap = getCentroidDistancesForSong(song);
            List<Centroid> rankedCentroids = rankCentroidsForSong(centroidSimilarityMap);
            Centroid mostSimilarCentroid = rankedCentroids.get(0);
            centroidPartition.get(mostSimilarCentroid).add(song);
        }
        return centroidPartition;
    }

    private Map<Song, Map<Centroid, Double>> reassignSongsToCentroids() {
        Map<Centroid, List<Song>> newCentroidPartition = new HashMap<>();
        Map<Song, Map<Centroid, Double>> songCentroidDistances = new HashMap<>();
        for (Centroid centroid : centroids) {
            List<Song> songsInPartition = new ArrayList<>();
            newCentroidPartition.put(centroid, songsInPartition);
            centroid.recalculateCentroid(updateablePartitionMap.get(centroid), normalizedPredictedSongs);
        }
        for (Song song : normalizedPredictedSongs.keySet()) {
            Map<Centroid, Double> centroidSimilarityMap = getCentroidDistancesForSong(song);
            List<Centroid> rankedCentroids = rankCentroidsForSong(centroidSimilarityMap);
            Centroid mostSimilarCentroid = rankedCentroids.get(0);
            newCentroidPartition.get(mostSimilarCentroid).add(song);
            songCentroidDistances.put(song, centroidSimilarityMap);
        }
        updateablePartitionMap = newCentroidPartition;
        return songCentroidDistances;
    }

    private Map<Centroid, Double> getCentroidDistancesForSong(Song song) {
        Map<Centroid, Double> centroidSimilarityMap = new HashMap<>();
        Map<String, Double> normalizedPredictedRatings = normalizedPredictedSongs.get(song);
        for (Centroid centroid : centroids) {
            Double distance = Calculations.calculateEuclideanDistance(centroid.getRatingsMap(), normalizedPredictedRatings, validUsers);
            centroidSimilarityMap.put(centroid, distance);
        }
        return centroidSimilarityMap;
    }

    private List<Centroid> rankCentroidsForSong(Map<Centroid, Double> centroidSimilarityMap) {
        List<Centroid> rankedCentroidSimilarity = new ArrayList<>(centroids);
        rankedCentroidSimilarity.sort( (a, b) -> { return centroidSimilarityMap.get(a).compareTo(centroidSimilarityMap.get(b)); });
        return rankedCentroidSimilarity;
    }

}
