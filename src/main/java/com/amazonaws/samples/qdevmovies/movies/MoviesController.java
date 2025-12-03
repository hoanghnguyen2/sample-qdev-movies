package com.amazonaws.samples.qdevmovies.movies;

import com.amazonaws.samples.qdevmovies.utils.MovieIconUtils;
import com.amazonaws.samples.qdevmovies.utils.PirateLanguageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class MoviesController {
    private static final Logger logger = LogManager.getLogger(MoviesController.class);

    @Autowired
    private MovieService movieService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping("/movies")
    public String getMovies(org.springframework.ui.Model model) {
        logger.info("Fetching movies");
        model.addAttribute("movies", movieService.getAllMovies());
        return "movies";
    }

    @GetMapping("/movies/{id}/details")
    public String getMovieDetails(@PathVariable("id") Long movieId, org.springframework.ui.Model model) {
        logger.info("Fetching details for movie ID: {}", movieId);
        
        Optional<Movie> movieOpt = movieService.getMovieById(movieId);
        if (!movieOpt.isPresent()) {
            logger.warn("Movie with ID {} not found", movieId);
            model.addAttribute("title", "Movie Not Found");
            model.addAttribute("message", "Movie with ID " + movieId + " was not found.");
            return "error";
        }
        
        Movie movie = movieOpt.get();
        model.addAttribute("movie", movie);
        model.addAttribute("movieIcon", MovieIconUtils.getMovieIcon(movie.getMovieName()));
        model.addAttribute("allReviews", reviewService.getReviewsForMovie(movie.getId()));
        
        return "movie-details";
    }

    /**
     * REST API endpoint for movie search
     * Supports JSON responses with pirate language option
     * @param name Movie name (partial match, case-insensitive)
     * @param id Movie ID (exact match)
     * @param genre Movie genre (partial match, case-insensitive)
     * @param pirate Enable pirate language mode
     * @return JSON response with search results
     */
    @GetMapping("/movies/search")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> searchMoviesApi(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String pirate) {
        
        logger.info("API search request - name: {}, id: {}, genre: {}, pirate: {}", name, id, genre, pirate);
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Validate search parameters
            if (!movieService.isValidSearchRequest(name, id, genre)) {
                String errorMessage = "At least one search parameter (name, id, or genre) must be provided";
                if (PirateLanguageUtils.isPirateMode(pirate, name)) {
                    errorMessage = PirateLanguageUtils.createPirateErrorMessage(errorMessage);
                }
                
                response.put("error", errorMessage);
                response.put("success", false);
                return ResponseEntity.badRequest().body(response);
            }
            
            // Perform search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Check if pirate mode is enabled
            boolean isPirateMode = PirateLanguageUtils.isPirateMode(pirate, name);
            
            // Convert to pirate language if needed
            if (isPirateMode) {
                searchResults = searchResults.stream()
                    .map(PirateLanguageUtils::toPirateMovie)
                    .collect(Collectors.toList());
            }
            
            // Create search criteria string for message
            StringBuilder criteria = new StringBuilder();
            if (name != null && !name.trim().isEmpty()) {
                criteria.append("name: ").append(name);
            }
            if (id != null && id > 0) {
                if (criteria.length() > 0) criteria.append(", ");
                criteria.append("id: ").append(id);
            }
            if (genre != null && !genre.trim().isEmpty()) {
                if (criteria.length() > 0) criteria.append(", ");
                criteria.append("genre: ").append(genre);
            }
            
            // Create response message
            String message;
            if (isPirateMode) {
                message = PirateLanguageUtils.createPirateSearchMessage(searchResults.size(), criteria.toString());
            } else {
                if (searchResults.isEmpty()) {
                    message = "No movies found matching the search criteria: " + criteria.toString();
                } else {
                    message = "Found " + searchResults.size() + " movie(s) matching: " + criteria.toString();
                }
            }
            
            response.put("success", true);
            response.put("message", message);
            response.put("count", searchResults.size());
            response.put("movies", searchResults);
            response.put("pirateMode", isPirateMode);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error during movie search: {}", e.getMessage(), e);
            
            String errorMessage = "An error occurred while searching for movies";
            if (PirateLanguageUtils.isPirateMode(pirate, name)) {
                errorMessage = PirateLanguageUtils.createPirateErrorMessage(errorMessage);
            }
            
            response.put("error", errorMessage);
            response.put("success", false);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * HTML form endpoint for movie search
     * Handles form submissions and redirects to search results page
     * @param name Movie name (partial match, case-insensitive)
     * @param id Movie ID (exact match)
     * @param genre Movie genre (partial match, case-insensitive)
     * @param pirate Enable pirate language mode
     * @param model Spring model for template rendering
     * @return Template name for search results
     */
    @GetMapping("/movies/search/form")
    public String searchMoviesForm(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String pirate,
            org.springframework.ui.Model model) {
        
        logger.info("Form search request - name: {}, id: {}, genre: {}, pirate: {}", name, id, genre, pirate);
        
        try {
            // Check if pirate mode is enabled
            boolean isPirateMode = PirateLanguageUtils.isPirateMode(pirate, name);
            
            // Validate search parameters
            if (!movieService.isValidSearchRequest(name, id, genre)) {
                String errorMessage = "Please provide at least one search criterion (name, ID, or genre)";
                if (isPirateMode) {
                    errorMessage = PirateLanguageUtils.createPirateErrorMessage(errorMessage);
                }
                
                model.addAttribute("error", errorMessage);
                model.addAttribute("movies", movieService.getAllMovies());
                model.addAttribute("pirateMode", isPirateMode);
                return "movies";
            }
            
            // Perform search
            List<Movie> searchResults = movieService.searchMovies(name, id, genre);
            
            // Convert to pirate language if needed
            if (isPirateMode) {
                searchResults = searchResults.stream()
                    .map(PirateLanguageUtils::toPirateMovie)
                    .collect(Collectors.toList());
            }
            
            // Create search criteria string for message
            StringBuilder criteria = new StringBuilder();
            if (name != null && !name.trim().isEmpty()) {
                criteria.append("name: ").append(name);
            }
            if (id != null && id > 0) {
                if (criteria.length() > 0) criteria.append(", ");
                criteria.append("id: ").append(id);
            }
            if (genre != null && !genre.trim().isEmpty()) {
                if (criteria.length() > 0) criteria.append(", ");
                criteria.append("genre: ").append(genre);
            }
            
            // Create response message
            String message;
            if (isPirateMode) {
                message = PirateLanguageUtils.createPirateSearchMessage(searchResults.size(), criteria.toString());
            } else {
                if (searchResults.isEmpty()) {
                    message = "No movies found matching your search criteria: " + criteria.toString();
                } else {
                    message = "Found " + searchResults.size() + " movie(s) matching: " + criteria.toString();
                }
            }
            
            model.addAttribute("movies", searchResults);
            model.addAttribute("searchMessage", message);
            model.addAttribute("isSearchResult", true);
            model.addAttribute("pirateMode", isPirateMode);
            model.addAttribute("searchCriteria", criteria.toString());
            
            return "movies";
            
        } catch (Exception e) {
            logger.error("Error during form movie search: {}", e.getMessage(), e);
            
            String errorMessage = "An error occurred while searching for movies. Please try again.";
            boolean isPirateMode = PirateLanguageUtils.isPirateMode(pirate, name);
            if (isPirateMode) {
                errorMessage = PirateLanguageUtils.createPirateErrorMessage(errorMessage);
            }
            
            model.addAttribute("error", errorMessage);
            model.addAttribute("movies", movieService.getAllMovies());
            model.addAttribute("pirateMode", isPirateMode);
            return "movies";
        }
    }
}