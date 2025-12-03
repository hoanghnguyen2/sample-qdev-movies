package com.amazonaws.samples.qdevmovies.utils;

import com.amazonaws.samples.qdevmovies.movies.Movie;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PirateLanguageUtilsTest {

    @Test
    public void testIsPirateModeWithParameter() {
        assertTrue(PirateLanguageUtils.isPirateMode("true", null));
        assertTrue(PirateLanguageUtils.isPirateMode("TRUE", null));
        assertTrue(PirateLanguageUtils.isPirateMode("1", null));
        
        assertFalse(PirateLanguageUtils.isPirateMode("false", null));
        assertFalse(PirateLanguageUtils.isPirateMode("0", null));
        assertFalse(PirateLanguageUtils.isPirateMode(null, null));
        assertFalse(PirateLanguageUtils.isPirateMode("", null));
    }

    @Test
    public void testIsPirateModeWithKeywords() {
        assertTrue(PirateLanguageUtils.isPirateMode(null, "pirate"));
        assertTrue(PirateLanguageUtils.isPirateMode(null, "PIRATE"));
        assertTrue(PirateLanguageUtils.isPirateMode(null, "arrr"));
        assertTrue(PirateLanguageUtils.isPirateMode(null, "ahoy"));
        assertTrue(PirateLanguageUtils.isPirateMode(null, "treasure"));
        assertTrue(PirateLanguageUtils.isPirateMode(null, "I love pirate movies"));
        
        assertFalse(PirateLanguageUtils.isPirateMode(null, "regular movie"));
        assertFalse(PirateLanguageUtils.isPirateMode(null, "drama"));
        assertFalse(PirateLanguageUtils.isPirateMode(null, null));
        assertFalse(PirateLanguageUtils.isPirateMode(null, ""));
    }

    @Test
    public void testToPirateLanguage() {
        assertEquals("treasure", PirateLanguageUtils.toPirateLanguage("movie"));
        assertEquals("treasures", PirateLanguageUtils.toPirateLanguage("movies"));
        assertEquals("captain", PirateLanguageUtils.toPirateLanguage("director"));
        assertEquals("hunt", PirateLanguageUtils.toPirateLanguage("search"));
        assertEquals("bounty", PirateLanguageUtils.toPirateLanguage("results"));
        assertEquals("type of adventure", PirateLanguageUtils.toPirateLanguage("genre"));
    }

    @Test
    public void testToPirateLanguageCaseInsensitive() {
        assertEquals("treasure", PirateLanguageUtils.toPirateLanguage("Movie"));
        assertEquals("treasure", PirateLanguageUtils.toPirateLanguage("MOVIE"));
        assertEquals("captain", PirateLanguageUtils.toPirateLanguage("Director"));
        assertEquals("captain", PirateLanguageUtils.toPirateLanguage("DIRECTOR"));
    }

    @Test
    public void testToPirateLanguageWholeWordsOnly() {
        // Should only replace whole words, not parts of words
        assertEquals("moviemaker", PirateLanguageUtils.toPirateLanguage("moviemaker"));
        assertEquals("directorial", PirateLanguageUtils.toPirateLanguage("directorial"));
    }

    @Test
    public void testToPirateLanguageMultipleWords() {
        String input = "Search for movies by director and genre";
        String result = PirateLanguageUtils.toPirateLanguage(input);
        assertTrue(result.contains("hunt"));
        assertTrue(result.contains("treasures"));
        assertTrue(result.contains("captain"));
        assertTrue(result.contains("type of adventure"));
    }

    @Test
    public void testToPirateLanguageNullAndEmpty() {
        assertNull(PirateLanguageUtils.toPirateLanguage(null));
        assertEquals("", PirateLanguageUtils.toPirateLanguage(""));
        assertEquals("   ", PirateLanguageUtils.toPirateLanguage("   "));
    }

    @Test
    public void testToPirateMovie() {
        Movie originalMovie = new Movie(1L, "Test Movie", "John Director", 2023, "Drama", 
                                      "A great movie about adventure", 120, 4.5);
        
        Movie pirateMovie = PirateLanguageUtils.toPirateMovie(originalMovie);
        
        assertNotNull(pirateMovie);
        assertEquals(originalMovie.getId(), pirateMovie.getId());
        assertEquals(originalMovie.getMovieName(), pirateMovie.getMovieName()); // Name should stay the same
        assertEquals(originalMovie.getYear(), pirateMovie.getYear());
        assertEquals(originalMovie.getDuration(), pirateMovie.getDuration());
        assertEquals(originalMovie.getImdbRating(), pirateMovie.getImdbRating(), 0.01);
        
        // These should be translated
        assertNotEquals(originalMovie.getDirector(), pirateMovie.getDirector());
        assertNotEquals(originalMovie.getGenre(), pirateMovie.getGenre());
        assertNotEquals(originalMovie.getDescription(), pirateMovie.getDescription());
        
        // Check specific translations
        assertTrue(pirateMovie.getDirector().contains("John captain"));
        assertTrue(pirateMovie.getDescription().contains("treasure"));
    }

    @Test
    public void testToPirateMovieNull() {
        assertNull(PirateLanguageUtils.toPirateMovie(null));
    }

    @Test
    public void testAddPirateGreeting() {
        String message = "Welcome to the movie search";
        String result = PirateLanguageUtils.addPirateGreeting(message);
        
        assertNotNull(result);
        assertTrue(result.startsWith("Ahoy matey!") || 
                  result.startsWith("Avast ye!") || 
                  result.startsWith("Batten down the hatches!") || 
                  result.startsWith("Shiver me timbers!"));
        assertTrue(result.contains(message));
    }

    @Test
    public void testAddPirateEnding() {
        String message = "Search completed successfully";
        String result = PirateLanguageUtils.addPirateEnding(message);
        
        assertNotNull(result);
        assertTrue(result.endsWith("Arrr!") || 
                  result.endsWith("Yo ho ho!") || 
                  result.endsWith("Savvy?") || 
                  result.endsWith("Aye aye, captain!"));
        assertTrue(result.contains(message));
    }

    @Test
    public void testCreatePirateSearchMessage() {
        String criteria = "name: pirate, genre: adventure";
        
        // Test no results
        String noResults = PirateLanguageUtils.createPirateSearchMessage(0, criteria);
        assertTrue(noResults.contains("No treasure found"));
        assertTrue(noResults.contains(criteria));
        
        // Test single result
        String singleResult = PirateLanguageUtils.createPirateSearchMessage(1, criteria);
        assertTrue(singleResult.contains("Found 1 treasure"));
        assertTrue(singleResult.contains(criteria));
        
        // Test multiple results
        String multipleResults = PirateLanguageUtils.createPirateSearchMessage(5, criteria);
        assertTrue(multipleResults.contains("Discovered 5 treasures"));
        assertTrue(multipleResults.contains(criteria));
    }

    @Test
    public void testCreatePirateErrorMessage() {
        String error = "Invalid search parameters";
        String result = PirateLanguageUtils.createPirateErrorMessage(error);
        
        assertNotNull(result);
        assertTrue(result.contains("Trouble on the high seas"));
        assertTrue(result.contains("cursed"));
        assertTrue(result.startsWith("Ahoy matey!") || 
                  result.startsWith("Avast ye!") || 
                  result.startsWith("Batten down the hatches!") || 
                  result.startsWith("Shiver me timbers!"));
    }

    @Test
    public void testPirateLanguageConsistency() {
        // Test that the same input always produces the same output (except for random greetings/endings)
        String input = "Search for movies by director";
        String result1 = PirateLanguageUtils.toPirateLanguage(input);
        String result2 = PirateLanguageUtils.toPirateLanguage(input);
        
        assertEquals(result1, result2);
    }

    @Test
    public void testPirateLanguagePreservesFormatting() {
        String input = "Movie: The Great Adventure\nDirector: John Smith\nGenre: Action";
        String result = PirateLanguageUtils.toPirateLanguage(input);
        
        // Should preserve line breaks and formatting
        assertTrue(result.contains("\n"));
        assertTrue(result.contains(":"));
    }
}