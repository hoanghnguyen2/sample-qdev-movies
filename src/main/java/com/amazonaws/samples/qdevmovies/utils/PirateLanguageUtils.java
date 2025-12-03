package com.amazonaws.samples.qdevmovies.utils;

import com.amazonaws.samples.qdevmovies.movies.Movie;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Utility class for converting text to pirate language
 * Activated when pirate mode is enabled via query parameters or keywords
 */
public class PirateLanguageUtils {
    private static final Logger logger = LogManager.getLogger(PirateLanguageUtils.class);
    
    // Pirate language translations
    private static final Map<String, String> PIRATE_TRANSLATIONS = new HashMap<>();
    
    static {
        // Common words and phrases
        PIRATE_TRANSLATIONS.put("movie", "treasure");
        PIRATE_TRANSLATIONS.put("movies", "treasures");
        PIRATE_TRANSLATIONS.put("film", "treasure");
        PIRATE_TRANSLATIONS.put("films", "treasures");
        PIRATE_TRANSLATIONS.put("search", "hunt");
        PIRATE_TRANSLATIONS.put("find", "discover");
        PIRATE_TRANSLATIONS.put("found", "discovered");
        PIRATE_TRANSLATIONS.put("results", "bounty");
        PIRATE_TRANSLATIONS.put("no results", "no treasure found");
        PIRATE_TRANSLATIONS.put("director", "captain");
        PIRATE_TRANSLATIONS.put("year", "year of sailing");
        PIRATE_TRANSLATIONS.put("genre", "type of adventure");
        PIRATE_TRANSLATIONS.put("duration", "length of voyage");
        PIRATE_TRANSLATIONS.put("rating", "crew's approval");
        PIRATE_TRANSLATIONS.put("description", "tale");
        PIRATE_TRANSLATIONS.put("details", "treasure map");
        PIRATE_TRANSLATIONS.put("view", "examine");
        PIRATE_TRANSLATIONS.put("back", "return to ship");
        PIRATE_TRANSLATIONS.put("error", "trouble on the high seas");
        PIRATE_TRANSLATIONS.put("not found", "lost at sea");
        PIRATE_TRANSLATIONS.put("invalid", "cursed");
        PIRATE_TRANSLATIONS.put("parameter", "compass reading");
        PIRATE_TRANSLATIONS.put("parameters", "compass readings");
    }
    
    // Pirate greetings and phrases
    private static final String[] PIRATE_GREETINGS = {
        "Ahoy matey!",
        "Avast ye!",
        "Batten down the hatches!",
        "Shiver me timbers!"
    };
    
    private static final String[] PIRATE_ENDINGS = {
        "Arrr!",
        "Yo ho ho!",
        "Savvy?",
        "Aye aye, captain!"
    };
    
    /**
     * Check if pirate mode should be activated based on query parameters or keywords
     * @param pirateParam The pirate query parameter value
     * @param searchText Any search text that might contain pirate keywords
     * @return true if pirate mode should be activated
     */
    public static boolean isPirateMode(String pirateParam, String searchText) {
        // Check explicit pirate parameter
        if (pirateParam != null && ("true".equalsIgnoreCase(pirateParam) || "1".equals(pirateParam))) {
            return true;
        }
        
        // Check for pirate keywords in search text
        if (searchText != null) {
            String lowerText = searchText.toLowerCase();
            return lowerText.contains("pirate") || lowerText.contains("arrr") || 
                   lowerText.contains("ahoy") || lowerText.contains("treasure");
        }
        
        return false;
    }
    
    /**
     * Convert text to pirate language
     * @param text The original text to convert
     * @return The text converted to pirate language
     */
    public static String toPirateLanguage(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        
        logger.debug("Converting text to pirate language: {}", text);
        
        String pirateText = text;
        
        // Apply word replacements (case-insensitive)
        for (Map.Entry<String, String> entry : PIRATE_TRANSLATIONS.entrySet()) {
            String original = entry.getKey();
            String pirate = entry.getValue();
            
            // Replace whole words only (case-insensitive)
            pirateText = pirateText.replaceAll("(?i)\\b" + original + "\\b", pirate);
        }
        
        return pirateText;
    }
    
    /**
     * Convert a movie object to pirate language version
     * @param movie The original movie
     * @return A new movie with pirate language descriptions
     */
    public static Movie toPirateMovie(Movie movie) {
        if (movie == null) {
            return null;
        }
        
        return new Movie(
            movie.getId(),
            movie.getMovieName(), // Keep original movie name
            toPirateLanguage(movie.getDirector()),
            movie.getYear(),
            toPirateLanguage(movie.getGenre()),
            toPirateLanguage(movie.getDescription()),
            movie.getDuration(),
            movie.getImdbRating()
        );
    }
    
    /**
     * Add pirate greeting to a message
     * @param message The original message
     * @return Message with pirate greeting
     */
    public static String addPirateGreeting(String message) {
        if (message == null || message.trim().isEmpty()) {
            return PIRATE_GREETINGS[0] + " " + message;
        }
        
        int greetingIndex = (int) (Math.random() * PIRATE_GREETINGS.length);
        return PIRATE_GREETINGS[greetingIndex] + " " + message;
    }
    
    /**
     * Add pirate ending to a message
     * @param message The original message
     * @return Message with pirate ending
     */
    public static String addPirateEnding(String message) {
        if (message == null || message.trim().isEmpty()) {
            return message + " " + PIRATE_ENDINGS[0];
        }
        
        int endingIndex = (int) (Math.random() * PIRATE_ENDINGS.length);
        return message + " " + PIRATE_ENDINGS[endingIndex];
    }
    
    /**
     * Create a pirate-style search results message
     * @param count Number of results found
     * @param searchCriteria The search criteria used
     * @return Pirate-style message about search results
     */
    public static String createPirateSearchMessage(int count, String searchCriteria) {
        if (count == 0) {
            return addPirateEnding("No treasure found with yer compass readings: " + searchCriteria);
        } else if (count == 1) {
            return addPirateEnding("Found 1 treasure matching yer hunt: " + searchCriteria);
        } else {
            return addPirateEnding("Discovered " + count + " treasures in yer bounty hunt: " + searchCriteria);
        }
    }
    
    /**
     * Create a pirate-style error message
     * @param error The original error message
     * @return Pirate-style error message
     */
    public static String createPirateErrorMessage(String error) {
        return addPirateGreeting("Trouble on the high seas! " + toPirateLanguage(error));
    }
}