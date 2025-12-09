package OutputGenerators.OutputTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import KMeansClustering.Centroid;
import KMeansClustering.CentroidBuilder;
import KMeansClustering.PartitionBuilder;
import RatingServices.RatingService;
import Repository.Song;

public class SongRecommendations implements Printable {

    private Map<Song, Map<String, Double>> normalizedPredictedSongs;
    private RatingService ratingService;
    private List<String> centroidSeeds;
    private List<Centroid> centroids;
    private Map<Centroid, List<Song>> updateablePartitionMap;

    public SongRecommendations (RatingService ratingService, List<String> centroidSeeds) {
        this.ratingService = ratingService;
        this.centroidSeeds = centroidSeeds;
        normalizedPredictedSongs = ratingService.getPredictionService().getCompleteNormalizedPredictedSongs();

        CentroidBuilder centroidBuilder = new CentroidBuilder(centroidSeeds, ratingService, normalizedPredictedSongs);
        centroids = centroidBuilder.initializeCentroids();

        if (normalizedPredictedSongs.size() < centroidSeeds.size() + 1) {
            throw new IllegalArgumentException("there are no songs to recommend. Songs may have been removed. Try with a larger file or fewer selections.");
        }
    }

    private List<Song> removeCentroidsFromPartition(List<Song> partition) {
        List<Song> songsToRecommend = new ArrayList<>(partition);
        Iterator<Song> iterator = songsToRecommend.iterator();
        while (iterator.hasNext()) {
            String songName = iterator.next().getName();
            if (centroidSeeds.contains(songName)) {
                iterator.remove();
            }
        }
        return songsToRecommend;
    }

    @Override
    public String toString() {
        String output = "user choice,recommendation\n";
        PartitionBuilder partitionBuilder = new PartitionBuilder(centroids, normalizedPredictedSongs, ratingService.getValidUserList());
        updateablePartitionMap = partitionBuilder.buildPartitionMap();
        Collections.sort(centroids);
        for (Centroid centroid : centroids) {
            List<Song> songPartition = updateablePartitionMap.get(centroid);
            List<Song> songsToRecommend = removeCentroidsFromPartition(songPartition);
            Collections.sort(songsToRecommend);
            for (Song song : songsToRecommend) {
                output += centroid.getInitialSelection() + "," + song.getName() + "\n";
            }
        }
        return output;
    }

}
