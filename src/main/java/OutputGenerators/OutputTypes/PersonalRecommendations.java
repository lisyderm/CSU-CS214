package OutputGenerators.OutputTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import KMeansClustering.Centroid;
import KMeansClustering.CentroidBuilder;
import KMeansClustering.PartitionBuilder;
import RatingServices.Calculations;
import RatingServices.RatingService;
import Repository.Song;

public class PersonalRecommendations implements Printable {

    private Map<Song, Map<String, Double>> normalizedPredictedSongs;
    private RatingService ratingService;
    private List<String> userSelections;
    private List<Centroid> centroids;
    private Map<Centroid, List<Song>> updateablePartitionMap;

    public PersonalRecommendations (RatingService ratingService, List<String> userInputCentroidQuanitityAndSelectedSongs) {
        this.ratingService = ratingService;

        userSelections = new ArrayList<>(userInputCentroidQuanitityAndSelectedSongs.subList(1,userInputCentroidQuanitityAndSelectedSongs.size()));
        int selectedCentroidNumber = Integer.parseInt(userInputCentroidQuanitityAndSelectedSongs.get(0));

        if (selectedCentroidNumber < 1) throw new IllegalArgumentException("at least one centroid needed");
        if (userSelections.size() < selectedCentroidNumber) throw new IllegalArgumentException("fewer user selected songs than centroid number");

        List<String> centroidSeeds = new ArrayList<>(userSelections.subList(0, selectedCentroidNumber));
        normalizedPredictedSongs = ratingService.getPredictionService().getCompleteNormalizedPredictedSongs();

        CentroidBuilder centroidBuilder = new CentroidBuilder(centroidSeeds, ratingService, normalizedPredictedSongs);
        centroids = centroidBuilder.initializeCentroids();

        if (normalizedPredictedSongs.size() < centroidSeeds.size() + 1) {
            throw new IllegalArgumentException("there are no songs to recommend. Songs may have been removed. Try with a larger file or fewer selections.");
        }
    }

    @Override
    public String toString() {
        String output = "";
        PartitionBuilder partitionBuilder = new PartitionBuilder(centroids, normalizedPredictedSongs, ratingService.getValidUserList());
        updateablePartitionMap = partitionBuilder.buildPartitionMap();
        List<Song> recommendedSongs = buildRecommendedSongs();
        recommendedSongs.sort( (b, a) -> { return a.getDistanceFromNearest().compareTo(b.getDistanceFromNearest()); });
        int count = 0;
        for (Song song : recommendedSongs) {
            if (count >= 20) {
                break;
            }
            else {
                output += song.getName() + "\n";
                count++;
            }
        }
        return output;
    }

    private List<Song> buildRecommendedSongs() {
        List<Song> recommendedSongs = new ArrayList<>();
        for (String songName : userSelections) {
            Song targetSong = ratingService.findSongByName(songName);
            for (Map.Entry<Centroid, List<Song>> mapElement : updateablePartitionMap.entrySet()) {
                List<Song> partition = mapElement.getValue();
                recommendedSongs = recommendSongsInPartition(partition, targetSong, recommendedSongs);
            }
        }
        return recommendedSongs;
    }

    private List<Song> recommendSongsInPartition(List<Song> partition, Song targetSong, List<Song> recommendedSongs) {
        if (partition.contains(targetSong)) {
            for (Song songToCompare : partition) {
                if (!songToCompare.equals(targetSong) && !userSelections.contains(songToCompare.getName())) {
                    Double distance = 1.0 / Calculations.calculateEuclideanDistance(normalizedPredictedSongs.get(targetSong), normalizedPredictedSongs.get(songToCompare), ratingService.getValidUserList());
                    songToCompare.setDistanceFromNearestSelection(distance);
                    if (!recommendedSongs.contains(songToCompare)) recommendedSongs.add(songToCompare);
                }
            }
        }
        return recommendedSongs;
    }

}
