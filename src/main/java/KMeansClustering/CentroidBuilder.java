package KMeansClustering;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import RatingServices.RatingService;
import Repository.Song;

public class CentroidBuilder {

    private List<String> centroidSeeds;
    private RatingService ratingService;
    private Map<Song, Map<String, Double>> normalizedPredictedSongs;

    public CentroidBuilder (List<String> centroidSeeds, RatingService ratingService, Map<Song, Map<String, Double>> normalizedPredictedSongs) {
        this.centroidSeeds = centroidSeeds;
        this.ratingService = ratingService;
        this.normalizedPredictedSongs = normalizedPredictedSongs;
    }


    public List<Centroid> initializeCentroids() {
        List<Centroid> centroids = new ArrayList<>();
        for (String seed : centroidSeeds) {
            Song song = ratingService.findSongByName(seed);
            if (!normalizedPredictedSongs.containsKey(song)){
                throw new IllegalArgumentException("selected songs must have more than one distinct rating");
            }
            Centroid centroid = new Centroid(seed, normalizedPredictedSongs.get(song));
            centroids.add(centroid);
        }
        return centroids;
    }
}