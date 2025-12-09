import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import InputReaders.ArgParserInitializer;
import InputReaders.CsvReader;
import OutputGenerators.OutputTypes.PersonalRecommendations;
import OutputGenerators.OutputTypes.SongAnalysis;
import OutputGenerators.OutputTypes.SongRecommendations;
import OutputGenerators.OutputTypes.SongSimilarity;
import OutputGenerators.OutputTypes.UserAnalysis;
import OutputGenerators.OutputTypes.UserPredictions;
import RatingServices.Calculations;
import RatingServices.RatingService;
import Repository.RatingRepository;
import Repository.Song;
import Repository.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TestCs214Project {

    @Test
    public void testNoArguments() {
        String[] args = {};
        assertThrows(IllegalArgumentException.class,
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testSingleArgument() {
        String[] args = {"onefile.csv"};
        assertThrows(IllegalArgumentException.class,
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testWrongThirdArgument() {
        String[] args = {"onefile.csv", "twofile.csv", "threefile.csv"};
        assertThrows(IllegalArgumentException.class,
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testTooManyArguments() {
        String[] args = {"onefile.csv", "twofile.csv", "-a", "extraArgument.csv"};
        assertThrows(IllegalArgumentException.class,
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testArgsTooShort() {
        String[] args = {"csv", "twofile.csv"};
        assertThrows(IllegalArgumentException.class,
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testSongRecommendationArgRequiresFourthArg() {
        String[] args = {"input.csv", "file.csv", "-r"};
        assertThrows(IllegalArgumentException.class,
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testFileNotCSV() {
        String[] args = {"hello.png", "twofile.csv"};
        assertThrows(IllegalArgumentException.class,
            () -> new ArgParserInitializer(args)
        );
    }

        @Test
    public void testOutputFileDirectoryNotFound() {
        String[] args = {"database/tests/test1.csv", "notARealDirectory/output.csv"};
        assertThrows(IllegalArgumentException.class, 
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testInputFileNotFound() {
        String[] args = {"database/tests/NotARealFile.csv", "output.csv"};
        assertThrows(IllegalArgumentException.class, 
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testInputFileIsEmpty() {
        String[] args = {"database/tests/test-emptyfile.csv", "output.csv"};
        assertThrows(IllegalArgumentException.class, 
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testDuplicateSelectionInCentroidsSongRecommendations() {
        String[] args = {"database/tests/rockpop.csv", "output.csv", "-r", "Bohemian Rhapsody", "Bohemian Rhapsody"};
        assertThrows(IllegalArgumentException.class, 
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testDuplicateSelectionInCentroidsPersonalRecommendations() {
        String[] args = {"database/tests/rockpop.csv", "output.csv", "-s", "2", "Bohemian Rhapsody", "Bohemian Rhapsody"};
        assertThrows(IllegalArgumentException.class, 
            () -> new ArgParserInitializer(args)
        );
    }

        @Test
    public void testDuplicateSelections() {
        String[] args = {"database/tests/rockpop.csv", "output.csv", "-s", "2", "Bohemian Rhapsody", "Stairway to Heaven", "Bohemian Rhapsody"};
        assertThrows(IllegalArgumentException.class, 
            () -> new ArgParserInitializer(args)
        );
    }

    @Test
    public void testCalculateAverageRating() {
        Map<String, Double> ratings = new HashMap<>();
        ratings.put("abe",1.0);
        ratings.put("bob",2.0);
        ratings.put("chuck",3.0);
        double avg = Calculations.calculateAverageRating(ratings);
        // 6 / 3 = 2.0
        assertEquals(2.0, avg);
    }

    @Test
    public void testCalculateAverageRatingNoRating() {
        Map<String, Double> ratings = new HashMap<>();
        double avg = Calculations.calculateAverageRating(ratings);
        assertEquals(0.0, avg);
    }

    @Test
    public void testCalculatePopulationStandardDeviation() {
        Map<String, Double> ratings = new HashMap<>();
        ratings.put("abe",1.0);
        ratings.put("bob",2.0);
        ratings.put("chuck",3.0);
        double populationStandardDeviation = Calculations.calculatePopulationStandardDeviation(ratings);
        // the square root of ((0 squared plus 1 squared plus 1 squared) divided by n=3)
        assertEquals(0.816496580927726, populationStandardDeviation);
    }

    @Test
    public void testCalculatePopulationStandardDeviationNoRatings() {
        Map<String, Double> ratings = new HashMap<>();
        double populationStandardDeviation = Calculations.calculatePopulationStandardDeviation(ratings);
        assertEquals(0.0, populationStandardDeviation);
    }

    // Test Song constructor, getUserRatings, getName
    @Test
    public void testGenerateNewSong() {
        Song newSong = new Song("song1","user1",5.0);
        String name = newSong.getName();
        assertEquals("song1", name);
        Map<String, Double> ratings = newSong.getUserRatings();
        Map<String, Double> expectedMap = new HashMap<>();
        expectedMap.put("user1",5.0);
        assertEquals(expectedMap, ratings);
    }

    // Test User constructor, getSongRatings, getName
    @Test
    public void testGenerateNewUser() {
        User newUser = new User("userTest","firstSong",5.0);
        String name = newUser.getName();
        assertEquals("userTest", name);
        Map<String, Double> ratings = newUser.getSongRatings();
        Map<String, Double> expectedMap = new HashMap<>();
        expectedMap.put("firstSong",5.0);
        assertEquals(expectedMap, ratings);
    }

    // Test Song CompareTo method for alphabetizing
    @Test
    public void testSongCompareTo() {
        Song song1 = new Song("Zsong","user1",5.0);
        Song song2 = new Song("Asong","user1",4.0);
        assertTrue(song1.compareTo(song2) > 0);
    }

    // Test User CompareTo method for alphabetizing
    @Test
    public void testUserCompareTo() {
        User user1 = new User("Zuser","song1",5.0);
        User user2 = new User("Auser","song1",4.0);
        assertTrue(user1.compareTo(user2) > 0);
    }

    // Test User Variation checker
    @Test
    public void testUserRatingVariationNone() {
        User user1 = new User("Zuser","song1",5.0);
        assertEquals(0,user1.getRatingVariation());
    }

    // Test User Variation checker
    @Test
    public void testUserRatingVariationNonZero() {
        User user1 = new User("Zuser","song1",5.0);
        user1.addNewRating("song2",3.0);
        assertEquals(1,user1.getRatingVariation());
    }

    @Test
    public void testGenerateNormalizedRatingsSimple() {
        RatingRepository ratingRepository = new RatingRepository();
        ratingRepository.addNewRating("song1", "user1", 2.0);
        ratingRepository.addNewRating("song2", "user1", 3.0);
        ratingRepository.addNewRating("song4", "user1", 4.0);
        RatingService service = new RatingService(ratingRepository);
        User user1 = service.findUserByName("user1");
        Map<String, Double> expected = new HashMap<>();
        expected.put("song1", -1.224744871391589);
        expected.put("song2", 0.0);
        expected.put("song4", 1.224744871391589);
        Map<String, Double> actual = user1.getNormalizedRatings();
        assertEquals(expected,actual);
    }

    @Test
    // PA1 -- SongAnalysis for test1.csv
    public void testSongAnalysis() {
        CsvReader fileReader = new CsvReader("database/tests/test1.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        SongAnalysis output = new SongAnalysis(service);
        assertEquals("song,number of ratings,mean,standard deviation\n" +
                        "song1,4,3.25,1.0897247358851685\n" +
                        "song2,4,4.0,1.224744871391589\n", output.toString());
    }

    @Test
    // PA2 -- UserAnalysis output for test4.csv
    public void testUserAnalysis() {
        CsvReader fileReader = new CsvReader("database/tests/test4.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        UserAnalysis output = new UserAnalysis(service);
        assertEquals("username,song,rating\n" + //
                        "alex,All Star,3\n" + //
                        "alex,Bohemian Rhapsody,NaN\n" + //
                        "alex,Sweet Home Alabama,4\n" + //
                        "charlie,All Star,2\n" + //
                        "charlie,Bohemian Rhapsody,4\n" + //
                        "charlie,Sweet Home Alabama,3\n", output.toString());
    }

    @Test
    // PA3 -- SongSimilarity output for test2.csv
    public void testSongSimilarityFile2() {
        CsvReader fileReader = new CsvReader("database/tests/test2.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        SongSimilarity output = new SongSimilarity(service);
        assertEquals("name1,name2,similarity\n" + //
                        "Big Girls Don't Cry,I Gotta Feeling,2.0847431127488694\n" + //
                        "Big Girls Don't Cry,Suit & Tie,1.765045216243656\n" + //
                        "I Gotta Feeling,Suit & Tie,2.353393621658208\n", output.toString());
    }

    @Test
    // PA3 -- SongSimilarity output for test3.csv
    public void testSongSimilarityTest3() {
        CsvReader fileReader = new CsvReader("database/tests/test3.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        SongSimilarity output = new SongSimilarity(service);
        assertEquals("name1,name2,similarity\n" + //
                        "song1,song2,2.0\n" + 
                        "song1,song3,2.8284271247461903\n" +
                        "song1,song4,2.1213203435596424\n" + 
                        "song2,song3,NaN\n" + 
                        "song2,song4,2.1213203435596424\n" +
                        "song3,song4,NaN\n", output.toString());
    }

    @Test
    // PA3 -- SongSimilarity - 1 user should throw exception
    public void testOnlyOneUserThrowsExceptionForSongSimilarity() {
        CsvReader fileReader = new CsvReader("database/tests/test-only1user.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        assertThrows(IllegalArgumentException.class,
            () -> new SongSimilarity(service).toString()
        );
    }

    @Test
    // PA4
    public void testUserPredictionsTest5() {
        CsvReader fileReader = new CsvReader("database/tests/test5.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        UserPredictions output = new UserPredictions(service);
        assertEquals("song,user,predicted rating\n" + //
                        "All Star,charlie,4\n" + //
                        "Bohemian Rhapsody,alex,2\n" + //
                        "Sweet Home Alabama,alex,3\n" + //
                        "We Will Rock You,cameron,1\n" + //
                        "We Will Rock You,charlie,NaN\n", output.toString());
    }

    @Test
    // PA4 - error - less than two cooperative users
    public void testOnlyOneUserThrowsExceptionForUserPredictions() {
        CsvReader fileReader = new CsvReader("database/tests/test-only1user.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        assertThrows(IllegalArgumentException.class,
            () -> new UserPredictions(service).toString()
        );
    }

    @Test
    // PA4 - error - no predictions to be made
    public void testNoPredictionsThrowsExceptionForUserPredictions() {
        CsvReader fileReader = new CsvReader("database/tests/test1.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        assertThrows(IllegalArgumentException.class,
            () -> new UserPredictions(service).toString()
        );
    }

    @Test
    // PA4 - predicted rating above range results in 5
    public void testUserPredictionsCorrectTooHighPrediction() {
        CsvReader fileReader = new CsvReader("database/tests/test-predictionTooHigh.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        UserPredictions output = new UserPredictions(service);
        assertEquals("song,user,predicted rating\n" + //
                        "song6,user1,5\n", output.toString());
    }

    @Test
    // PA4 - predicted rating below range results in 1
    public void testUserPredictionsCorrectTooLowPrediction() {
        CsvReader fileReader = new CsvReader("database/tests/test-predictionTooLow.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        UserPredictions output = new UserPredictions(service);
        assertEquals("song,user,predicted rating\n" + //
                        "song6,user2,1\n", output.toString());
    }

    @Test
    // PA5 - works with 1 iteration
    public void testSongRecommendationsFile3() {
        CsvReader fileReader = new CsvReader("database/tests/PA5file3.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        List<String> centroids = new ArrayList<>();
        centroids.add("song3");
        centroids.add("song5");
        centroids.add("song6");
        SongRecommendations output = new SongRecommendations(service, centroids);
        assertEquals("user choice,recommendation\n" + //
                        "song3,song4\n" + //
                        "song6,song1\n" + //
                        "song6,song2\n", output.toString());
    }

    @Test
    // PA5 - works with 1 iteration
    public void testSongRecommendationsLargeDatasetProvided() {
        CsvReader fileReader = new CsvReader("database/tests/large_dataset.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        List<String> centroids = new ArrayList<>();
        centroids.add("song1");
        centroids.add("song2");
        centroids.add("song3");
        SongRecommendations output = new SongRecommendations(service, centroids);
        assertEquals("user choice,recommendation\n" + //
                        "song1,song6\n" + //
                        "song2,song4\n" + //
                        "song2,song8\n" + //
                        "song3,song5\n" + //
                        "song3,song7\n" + //
                        "song3,song9\n", output.toString());
    }

    @Test
    // PA6 - rock.csv dataset
    public void testPersonalRecommendationsRock() {
        CsvReader fileReader = new CsvReader("database/tests/rock.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        List<String> centroids = new ArrayList<>();
        centroids.add("1");
        centroids.add("Bohemian Rhapsody");
        PersonalRecommendations output = new PersonalRecommendations(service, centroids);
        assertEquals("Stairway to Heaven\nHotel California\nNovember Rain\n", output.toString());
    }

    @Test
    // PA6 - rockpop.csv dataset with only 3 recommendations due to use selection leaving initial centroid
    public void testPersonalRecommendationsRockPop1() {
        CsvReader fileReader = new CsvReader("database/tests/rockpop.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        List<String> centroids = new ArrayList<>();
        centroids.add("2");
        centroids.add("Bohemian Rhapsody");
        centroids.add("Stairway to Heaven");
        PersonalRecommendations output = new PersonalRecommendations(service, centroids);
        assertEquals("November Rain\nHotel California\nSweet Child O' Mine\n", output.toString());
    }

    @Test
    // PA6 - rockpop.csv dataset with all 6 other songs recommended
    public void testPersonalRecommendationsRockPop2() {
        CsvReader fileReader = new CsvReader("database/tests/rockpop.csv");
        RatingRepository ratingRepository = fileReader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        List<String> centroids = new ArrayList<>();
        centroids.add("2");
        centroids.add("Bohemian Rhapsody");
        centroids.add("Bad Romance");
        PersonalRecommendations output = new PersonalRecommendations(service, centroids);
        assertEquals("Shallow\n" + //
                        "Sweet Child O' Mine\n" + //
                        "Poker Face\n" + //
                        "Stairway to Heaven\n" + //
                        "November Rain\n" + //
                        "Hotel California\n", output.toString());
    }

    @Test
    public void testReadInputFile(){
        CsvReader reader = new CsvReader("database/tests/test1.csv");
        RatingRepository ratingRepository = reader.getRatingRepository();
        RatingService service = new RatingService(ratingRepository);
        assertEquals(2,reader.getRatingRepository().getSongs().size());
        assertEquals(2,service.getValidSongList().size());
        assertEquals(3, service.getValidUserList().size());
    }

    @Test
    public void testWeightedAverage(){
        RatingRepository ratingRepository = new RatingRepository();
        ratingRepository.addNewRating("song2", "user1", 3.0);
        ratingRepository.addNewRating("song2", "user2", 3.0);
        ratingRepository.addNewRating("song5", "user5", 3.0);
        ratingRepository.addNewRating("song6", "user5", 2.0);
        RatingService ratingServce = new RatingService(ratingRepository);
        User user5 = ratingServce.findUserByName("user5");
        Song song2 = ratingServce.findSongByName("song2");
        Double weightedAverage = Calculations.calculateWeightedAverage(song2, user5);
        assertEquals(3.0, Calculations.calculateAverageRating(song2.getUserRatings()));
        assertEquals(2.5, Calculations.calculateAverageRating(user5.getSongRatings()));
        assertEquals(2, song2.getUserRatings().size());
        assertEquals(2, user5.getSongRatings().size());
        assertEquals(3.0,weightedAverage);
    }
}