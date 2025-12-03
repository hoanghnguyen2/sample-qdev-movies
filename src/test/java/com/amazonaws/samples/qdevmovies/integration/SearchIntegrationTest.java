package com.amazonaws.samples.qdevmovies.integration;

import com.amazonaws.samples.qdevmovies.movies.Movie;
import com.amazonaws.samples.qdevmovies.movies.MovieService;
import com.amazonaws.samples.qdevmovies.utils.PirateLanguageUtils;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test to verify the search functionality works end-to-end
 */
public class SearchIntegrationTest {

    @Test
    public void testBasicSearchFunctionality() {
        // Test MovieService search functionality
        MovieService movieService = new MovieService();
        
        // Test search by name
        List<Movie> results = movieService.searchMoviesByName("Prison");
        assertNotNull(results);
        assertFalse(results.isEmpty());
        
        // Test search validation
        assertTrue(movieService.isValidSearchRequest("test", null, null));
        assertFalse(movieService.isValidSearchRequest(null, null, null));
        
        // Test pirate language functionality
        assertTrue(PirateLanguageUtils.isPirateMode("true", null));
        assertFalse(PirateLanguageUtils.isPirateMode("false", null));
        
        String pirateText = PirateLanguageUtils.toPirateLanguage("movie director");
        assertTrue(pirateText.contains("treasure"));
        assertTrue(pirateText.contains("captain"));
        
        System.out.println("✅ All search functionality tests passed!");
    }

    @Test
    public void testPirateLanguageIntegration() {
        MovieService movieService = new MovieService();
        
        // Get a movie and convert to pirate language
        List<Movie> movies = movieService.getAllMovies();
        assertFalse(movies.isEmpty());
        
        Movie originalMovie = movies.get(0);
        Movie pirateMovie = PirateLanguageUtils.toPirateMovie(originalMovie);
        
        assertNotNull(pirateMovie);
        assertEquals(originalMovie.getId(), pirateMovie.getId());
        assertEquals(originalMovie.getMovieName(), pirateMovie.getMovieName());
        
        // Director should be translated
        assertNotEquals(originalMovie.getDirector(), pirateMovie.getDirector());
        
        System.out.println("✅ Pirate language integration test passed!");
    }

    @Test
    public void testSearchWithMultipleCriteria() {
        MovieService movieService = new MovieService();
        
        // Test search with multiple criteria
        List<Movie> results = movieService.searchMovies("The", null, "Drama");
        assertNotNull(results);
        
        // All results should match both criteria
        for (Movie movie : results) {
            assertTrue(movie.getMovieName().toLowerCase().contains("the"));
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
        
        System.out.println("✅ Multi-criteria search test passed!");
    }
}