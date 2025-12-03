package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MovieServiceTest {

    private MovieService movieService;

    @BeforeEach
    public void setUp() {
        movieService = new MovieService();
    }

    @Test
    public void testGetAllMovies() {
        List<Movie> movies = movieService.getAllMovies();
        assertNotNull(movies);
        assertFalse(movies.isEmpty());
        // Should load movies from movies.json
        assertTrue(movies.size() > 0);
    }

    @Test
    public void testGetMovieById() {
        Optional<Movie> movie = movieService.getMovieById(1L);
        assertTrue(movie.isPresent());
        assertEquals(1L, movie.get().getId());
        assertEquals("The Prison Escape", movie.get().getMovieName());
    }

    @Test
    public void testGetMovieByIdNotFound() {
        Optional<Movie> movie = movieService.getMovieById(999L);
        assertFalse(movie.isPresent());
    }

    @Test
    public void testGetMovieByIdInvalid() {
        Optional<Movie> movie1 = movieService.getMovieById(null);
        assertFalse(movie1.isPresent());

        Optional<Movie> movie2 = movieService.getMovieById(0L);
        assertFalse(movie2.isPresent());

        Optional<Movie> movie3 = movieService.getMovieById(-1L);
        assertFalse(movie3.isPresent());
    }

    @Test
    public void testSearchMoviesByName() {
        List<Movie> results = movieService.searchMoviesByName("Prison");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNameCaseInsensitive() {
        List<Movie> results = movieService.searchMoviesByName("prison");
        assertNotNull(results);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesByNamePartialMatch() {
        List<Movie> results = movieService.searchMoviesByName("The");
        assertNotNull(results);
        assertTrue(results.size() > 1); // Should find multiple movies with "The" in the name
    }

    @Test
    public void testSearchMoviesByNameEmpty() {
        List<Movie> results = movieService.searchMoviesByName("");
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByNameNull() {
        List<Movie> results = movieService.searchMoviesByName(null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesByGenre() {
        List<Movie> results = movieService.searchMoviesByGenre("Drama");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        // All results should contain "Drama" in genre
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("drama"));
        }
    }

    @Test
    public void testSearchMoviesByGenreCaseInsensitive() {
        List<Movie> results = movieService.searchMoviesByGenre("drama");
        assertNotNull(results);
        assertTrue(results.size() > 0);
    }

    @Test
    public void testSearchMoviesByGenrePartialMatch() {
        List<Movie> results = movieService.searchMoviesByGenre("Sci");
        assertNotNull(results);
        assertTrue(results.size() > 0);
        // Should find Sci-Fi movies
        for (Movie movie : results) {
            assertTrue(movie.getGenre().toLowerCase().contains("sci"));
        }
    }

    @Test
    public void testSearchMovies() {
        // Test search with name only
        List<Movie> results1 = movieService.searchMovies("Prison", null, null);
        assertEquals(1, results1.size());
        assertEquals("The Prison Escape", results1.get(0).getMovieName());

        // Test search with ID only
        List<Movie> results2 = movieService.searchMovies(null, 1L, null);
        assertEquals(1, results2.size());
        assertEquals(1L, results2.get(0).getId());

        // Test search with genre only
        List<Movie> results3 = movieService.searchMovies(null, null, "Drama");
        assertTrue(results3.size() > 0);

        // Test search with multiple criteria
        List<Movie> results4 = movieService.searchMovies("Prison", 1L, "Drama");
        assertEquals(1, results4.size());
        assertEquals("The Prison Escape", results4.get(0).getMovieName());
    }

    @Test
    public void testSearchMoviesNoMatch() {
        List<Movie> results = movieService.searchMovies("NonExistentMovie", null, null);
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    public void testSearchMoviesMultipleCriteria() {
        // Search for a movie that matches name but not genre
        List<Movie> results = movieService.searchMovies("Prison", null, "Comedy");
        assertNotNull(results);
        assertTrue(results.isEmpty()); // Should be empty as "The Prison Escape" is Drama, not Comedy
    }

    @Test
    public void testSearchMoviesWithWhitespace() {
        List<Movie> results = movieService.searchMovies("  Prison  ", null, null);
        assertEquals(1, results.size());
        assertEquals("The Prison Escape", results.get(0).getMovieName());
    }

    @Test
    public void testIsValidSearchRequest() {
        // Valid requests
        assertTrue(movieService.isValidSearchRequest("Prison", null, null));
        assertTrue(movieService.isValidSearchRequest(null, 1L, null));
        assertTrue(movieService.isValidSearchRequest(null, null, "Drama"));
        assertTrue(movieService.isValidSearchRequest("Prison", 1L, "Drama"));

        // Invalid requests
        assertFalse(movieService.isValidSearchRequest(null, null, null));
        assertFalse(movieService.isValidSearchRequest("", null, null));
        assertFalse(movieService.isValidSearchRequest("   ", null, null));
        assertFalse(movieService.isValidSearchRequest(null, 0L, null));
        assertFalse(movieService.isValidSearchRequest(null, -1L, null));
        assertFalse(movieService.isValidSearchRequest(null, null, ""));
        assertFalse(movieService.isValidSearchRequest(null, null, "   "));
    }

    @Test
    public void testSearchMoviesPerformance() {
        // Test that search operations complete in reasonable time
        long startTime = System.currentTimeMillis();
        
        // Perform multiple searches
        for (int i = 0; i < 100; i++) {
            movieService.searchMovies("The", null, null);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete 100 searches in less than 1 second
        assertTrue(duration < 1000, "Search operations took too long: " + duration + "ms");
    }
}