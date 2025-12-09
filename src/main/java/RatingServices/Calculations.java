package RatingServices;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Repository.SimilarityComparable;
import Repository.Song;
import Repository.User;

public class Calculations {
    
    public static double calculateAverageRating(Map<String, Double> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        int total = 0;
        for (double rating : ratings.values()) {
            total += rating;
        }
        return (double) total / ratings.size();
    }

    public static double calculatePopulationStandardDeviation(Map<String, Double> ratings) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        double mean = calculateAverageRating(ratings);
        double sum = 0.0;
        int population = ratings.size();

        for (double rating : ratings.values()) {
            sum += Math.pow(rating - mean,2);
        }
       return Math.pow(sum/population, 0.5);
    }

    public static <T extends SimilarityComparable> double calculateEuclideanDistance(Map<String, Double> normalizedRatings1, Map<String, Double> normalizedRatings2, List<T> validList) {
        // the validList is the dimensions; the ratings maps are the vectors
        double distanceContributions = 0.0;
        int countOfSharedRatings = 0;
        for (SimilarityComparable distanceContributor : validList) {
            Double rating1 = normalizedRatings1.get(distanceContributor.getName());
            Double rating2 = normalizedRatings2.get(distanceContributor.getName());
            if (rating1 != null && rating2 != null) {
                distanceContributions += Math.pow(rating1 - rating2, 2.0);;
                countOfSharedRatings++;
            }            
        }
        if (countOfSharedRatings > 0) {
            return Math.sqrt(distanceContributions);
        }
        else return Double.NaN;
    }

    public static Map<String, Double> normalizeRatingsToThemselves(Map<String, Double> ratings) {
        //normalizedRating = (rating - mean) / stdDeviation;
        double mean = Calculations.calculateAverageRating(ratings);
        double standardDeviation = Calculations.calculatePopulationStandardDeviation(ratings);
        Map<String, Double> normalizedRatings = new HashMap<>();
        for (Map.Entry<String, Double> mapElement : ratings.entrySet()) {
            String song = mapElement.getKey();
            double rating = mapElement.getValue();
            double normalizedRating = (rating - mean) / standardDeviation;
            normalizedRatings.put(song, normalizedRating);
        }         
        return normalizedRatings;
    }

    public static Double calculateWeightedAverage(Song song, User targetUser) {
        //unpredicted = round((songMean * numSongRatings + userMean * numUserRatings) / (numSongRatings + numUserRatings))
        Map<String, Double> userRatings = song.getUserRatings();
        Map<String, Double> songRatings = targetUser.getSongRatings();
        double numSongRatings = userRatings.size();
        double songMean = Calculations.calculateAverageRating(userRatings);
        double numUserRatings = songRatings.size();
        double userMean = Calculations.calculateAverageRating(songRatings);
        long unpredicted = Math.round((songMean*numSongRatings + userMean*numUserRatings) / (numSongRatings + numUserRatings));
        Double roundedWeightedAverage = (double) unpredicted;
        if (roundedWeightedAverage < 1.0) roundedWeightedAverage = 1.0;
        if (roundedWeightedAverage > 5.0) roundedWeightedAverage = 5.0;
        return roundedWeightedAverage;
    }
}