package com.amazonaws.samples.qdevmovies.movies;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

@Service
public class MovieService {
    private static final Logger logger = LogManager.getLogger(MovieService.class);
    private final List<Movie> movies;
    private final Map<Long, Movie> movieMap;

    public MovieService() {
        this.movies = loadMoviesFromJson();
        this.movieMap = new HashMap<>();
        for (Movie movie : movies) {
            movieMap.put(movie.getId(), movie);
        }
    }

    private List<Movie> loadMoviesFromJson() {
        List<Movie> movieList = new ArrayList<>();
        try {
            InputStream inputStream = getClass().getClassLoader().getResourceAsStream("movies.json");
            if (inputStream != null) {
                Scanner scanner = new Scanner(inputStream, StandardCharsets.UTF_8.name());
                String jsonContent = scanner.useDelimiter("\\A").next();
                scanner.close();
                
                JSONArray moviesArray = new JSONArray(jsonContent);
                for (int i = 0; i < moviesArray.length(); i++) {
                    JSONObject movieObj = moviesArray.getJSONObject(i);
                    movieList.add(new Movie(
                        movieObj.getLong("id"),
                        movieObj.getString("movieName"),
                        movieObj.getString("director"),
                        movieObj.getInt("year"),
                        movieObj.getString("genre"),
                        movieObj.getString("description"),
                        movieObj.getInt("duration"),
                        movieObj.getDouble("imdbRating")
                    ));
                }
            }
        } catch (Exception e) {
            logger.error("Failed to load movies from JSON: {}", e.getMessage());
        }
        return movieList;
    }

    public List<Movie> getAllMovies() {
        return movies;
    }

    public Optional<Movie> getMovieById(Long id) {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        return Optional.ofNullable(movieMap.get(id));
    }

    /**
     * Search movies by multiple criteria
     * @param name Movie name (partial match, case-insensitive)
     * @param id Movie ID (exact match)
     * @param genre Movie genre (partial match, case-insensitive)
     * @return List of movies matching the criteria
     */
    public List<Movie> searchMovies(String name, Long id, String genre) {
        logger.info("Searching movies with criteria - name: {}, id: {}, genre: {}", name, id, genre);
        
        List<Movie> results = new ArrayList<>();
        
        for (Movie movie : movies) {
            boolean matches = true;
            
            // Check name criteria (partial match, case-insensitive)
            if (name != null && !name.trim().isEmpty()) {
                if (!movie.getMovieName().toLowerCase().contains(name.trim().toLowerCase())) {
                    matches = false;
                }
            }
            
            // Check ID criteria (exact match)
            if (id != null && id > 0) {
                if (movie.getId() != id.longValue()) {
                    matches = false;
                }
            }
            
            // Check genre criteria (partial match, case-insensitive)
            if (genre != null && !genre.trim().isEmpty()) {
                if (!movie.getGenre().toLowerCase().contains(genre.trim().toLowerCase())) {
                    matches = false;
                }
            }
            
            if (matches) {
                results.add(movie);
            }
        }
        
        logger.info("Found {} movies matching search criteria", results.size());
        return results;
    }

    /**
     * Search movies by name only
     * @param name Movie name (partial match, case-insensitive)
     * @return List of movies matching the name criteria
     */
    public List<Movie> searchMoviesByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return searchMovies(name, null, null);
    }

    /**
     * Search movies by genre only
     * @param genre Movie genre (partial match, case-insensitive)
     * @return List of movies matching the genre criteria
     */
    public List<Movie> searchMoviesByGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            return new ArrayList<>();
        }
        return searchMovies(null, null, genre);
    }

    /**
     * Validate search parameters
     * @param name Movie name parameter
     * @param id Movie ID parameter
     * @param genre Movie genre parameter
     * @return true if at least one valid parameter is provided
     */
    public boolean isValidSearchRequest(String name, Long id, String genre) {
        boolean hasName = name != null && !name.trim().isEmpty();
        boolean hasId = id != null && id > 0;
        boolean hasGenre = genre != null && !genre.trim().isEmpty();
        
        return hasName || hasId || hasGenre;
    }
}
