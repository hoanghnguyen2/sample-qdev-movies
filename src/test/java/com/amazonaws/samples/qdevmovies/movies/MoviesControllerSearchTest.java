package com.amazonaws.samples.qdevmovies.movies;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class MoviesControllerSearchTest {

    private MoviesController moviesController;
    private Model model;
    private MovieService mockMovieService;
    private ReviewService mockReviewService;

    @BeforeEach
    public void setUp() {
        moviesController = new MoviesController();
        model = new ExtendedModelMap();
        
        // Create mock services
        mockMovieService = new MovieService() {
            @Override
            public List<Movie> getAllMovies() {
                return Arrays.asList(
                    new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5),
                    new Movie(2L, "Pirate Adventure", "Captain Hook", 2022, "Adventure", "A pirate tale", 110, 4.0)
                );
            }
            
            @Override
            public Optional<Movie> getMovieById(Long id) {
                if (id == 1L) {
                    return Optional.of(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if (id == 2L) {
                    return Optional.of(new Movie(2L, "Pirate Adventure", "Captain Hook", 2022, "Adventure", "A pirate tale", 110, 4.0));
                }
                return Optional.empty();
            }
            
            @Override
            public List<Movie> searchMovies(String name, Long id, String genre) {
                if ("Test".equals(name)) {
                    return Arrays.asList(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if ("Pirate".equals(name)) {
                    return Arrays.asList(new Movie(2L, "Pirate Adventure", "Captain Hook", 2022, "Adventure", "A pirate tale", 110, 4.0));
                } else if (id != null && id == 1L) {
                    return Arrays.asList(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if ("Drama".equals(genre)) {
                    return Arrays.asList(new Movie(1L, "Test Movie", "Test Director", 2023, "Drama", "Test description", 120, 4.5));
                } else if ("NonExistent".equals(name)) {
                    return Arrays.asList(); // Empty list for no results
                }
                return getAllMovies();
            }
            
            @Override
            public boolean isValidSearchRequest(String name, Long id, String genre) {
                return (name != null && !name.trim().isEmpty()) || 
                       (id != null && id > 0) || 
                       (genre != null && !genre.trim().isEmpty());
            }
        };
        
        mockReviewService = new ReviewService() {
            @Override
            public List<Review> getReviewsForMovie(long movieId) {
                return Arrays.asList();
            }
        };
        
        // Inject mocks using reflection
        try {
            java.lang.reflect.Field movieServiceField = MoviesController.class.getDeclaredField("movieService");
            movieServiceField.setAccessible(true);
            movieServiceField.set(moviesController, mockMovieService);
            
            java.lang.reflect.Field reviewServiceField = MoviesController.class.getDeclaredField("reviewService");
            reviewServiceField.setAccessible(true);
            reviewServiceField.set(moviesController, mockReviewService);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock services", e);
        }
    }

    @Test
    public void testSearchMoviesApiWithName() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Test", null, null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
        assertNotNull(body.get("movies"));
        assertFalse((Boolean) body.get("pirateMode"));
    }

    @Test
    public void testSearchMoviesApiWithId() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, 1L, null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
    }

    @Test
    public void testSearchMoviesApiWithGenre() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, null, "Drama", null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(1, body.get("count"));
    }

    @Test
    public void testSearchMoviesApiWithPirateMode() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Test", null, null, "true");
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertTrue((Boolean) body.get("pirateMode"));
        String message = (String) body.get("message");
        assertTrue(message.contains("treasure") || message.contains("bounty"));
    }

    @Test
    public void testSearchMoviesApiWithPirateKeyword() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Pirate", null, null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertTrue((Boolean) body.get("pirateMode")); // Should be activated by "Pirate" keyword
    }

    @Test
    public void testSearchMoviesApiNoResults() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("NonExistent", null, null, null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertTrue((Boolean) body.get("success"));
        assertEquals(0, body.get("count"));
        String message = (String) body.get("message");
        assertTrue(message.contains("No movies found"));
    }

    @Test
    public void testSearchMoviesApiInvalidParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, null, null, null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        assertNotNull(body.get("error"));
    }

    @Test
    public void testSearchMoviesApiInvalidParametersWithPirate() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi(null, null, null, "true");
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertNotNull(body);
        assertFalse((Boolean) body.get("success"));
        String error = (String) body.get("error");
        assertTrue(error.contains("Ahoy") || error.contains("Avast") || error.contains("Trouble"));
    }

    @Test
    public void testSearchMoviesForm() {
        String result = moviesController.searchMoviesForm("Test", null, null, null, model);
        
        assertEquals("movies", result);
        assertNotNull(model.getAttribute("movies"));
        assertNotNull(model.getAttribute("searchMessage"));
        assertTrue((Boolean) model.getAttribute("isSearchResult"));
        assertFalse((Boolean) model.getAttribute("pirateMode"));
    }

    @Test
    public void testSearchMoviesFormWithPirate() {
        String result = moviesController.searchMoviesForm("Test", null, null, "true", model);
        
        assertEquals("movies", result);
        assertTrue((Boolean) model.getAttribute("pirateMode"));
        String message = (String) model.getAttribute("searchMessage");
        assertTrue(message.contains("treasure") || message.contains("bounty"));
    }

    @Test
    public void testSearchMoviesFormNoResults() {
        String result = moviesController.searchMoviesForm("NonExistent", null, null, null, model);
        
        assertEquals("movies", result);
        @SuppressWarnings("unchecked")
        List<Movie> movies = (List<Movie>) model.getAttribute("movies");
        assertTrue(movies.isEmpty());
        String message = (String) model.getAttribute("searchMessage");
        assertTrue(message.contains("No movies found"));
    }

    @Test
    public void testSearchMoviesFormInvalidParameters() {
        String result = moviesController.searchMoviesForm(null, null, null, null, model);
        
        assertEquals("movies", result);
        assertNotNull(model.getAttribute("error"));
        assertNotNull(model.getAttribute("movies")); // Should show all movies
    }

    @Test
    public void testSearchMoviesFormInvalidParametersWithPirate() {
        String result = moviesController.searchMoviesForm(null, null, null, "true", model);
        
        assertEquals("movies", result);
        String error = (String) model.getAttribute("error");
        assertTrue(error.contains("Ahoy") || error.contains("Avast") || error.contains("Trouble"));
        assertTrue((Boolean) model.getAttribute("pirateMode"));
    }

    @Test
    public void testSearchMoviesFormMultipleCriteria() {
        String result = moviesController.searchMoviesForm("Test", 1L, "Drama", null, model);
        
        assertEquals("movies", result);
        String criteria = (String) model.getAttribute("searchCriteria");
        assertTrue(criteria.contains("name: Test"));
        assertTrue(criteria.contains("id: 1"));
        assertTrue(criteria.contains("genre: Drama"));
    }

    @Test
    public void testSearchMoviesApiMultipleCriteria() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("Test", 1L, "Drama", null);
        
        assertEquals(HttpStatus.OK, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        String message = (String) body.get("message");
        assertTrue(message.contains("name: Test"));
        assertTrue(message.contains("id: 1"));
        assertTrue(message.contains("genre: Drama"));
    }

    @Test
    public void testSearchMoviesApiEmptyStringParameters() {
        ResponseEntity<Map<String, Object>> response = moviesController.searchMoviesApi("", null, "", null);
        
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        Map<String, Object> body = response.getBody();
        assertFalse((Boolean) body.get("success"));
    }

    @Test
    public void testSearchMoviesFormWhitespaceParameters() {
        String result = moviesController.searchMoviesForm("   ", null, "   ", null, model);
        
        assertEquals("movies", result);
        assertNotNull(model.getAttribute("error"));
    }
}