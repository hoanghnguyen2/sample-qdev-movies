# Movie Service - Spring Boot Demo Application

A simple movie catalog web application built with Spring Boot, demonstrating Java application development best practices with advanced search functionality and pirate language support.

## Features

- **Movie Catalog**: Browse 12 classic movies with detailed information
- **Advanced Search**: Search movies by name, ID, or genre with flexible filtering
- **Pirate Language Mode**: Fun pirate-themed language translation (🏴‍☠️ Arrr!)
- **REST API**: JSON API endpoints for programmatic access
- **HTML Form Interface**: User-friendly web forms for search functionality
- **Movie Details**: View comprehensive information including director, year, genre, duration, and description
- **Customer Reviews**: Each movie includes authentic customer reviews with ratings and avatars
- **Responsive Design**: Mobile-first design that works on all devices
- **Modern UI**: Dark theme with gradient backgrounds and smooth animations

## Technology Stack

- **Java 8**
- **Spring Boot 2.7.18**
- **Thymeleaf** for templating
- **Maven** for dependency management
- **Log4j 2.20.0**
- **JUnit 5.8.2**

## Quick Start

### Prerequisites

- Java 8 or higher
- Maven 3.6+

### Run the Application

```bash
git clone https://github.com/<youruser>/sample-qdev-movies.git
cd sample-qdev-movies
mvn spring-boot:run
```

The application will start on `http://localhost:8080`

### Access the Application

- **Movie List**: http://localhost:8080/movies
- **Movie Details**: http://localhost:8080/movies/{id}/details (where {id} is 1-12)
- **Search API**: http://localhost:8080/movies/search (REST API)
- **Search Form**: Use the search form on the main movies page

## API Endpoints

### Get All Movies
```
GET /movies
```
Returns an HTML page displaying all movies with ratings and basic information, including a search form.

### Get Movie Details
```
GET /movies/{id}/details
```
Returns an HTML page with detailed movie information and customer reviews.

**Parameters:**
- `id` (path parameter): Movie ID (1-12)

**Example:**
```
http://localhost:8080/movies/1/details
```

### Search Movies (REST API)
```
GET /movies/search
```
Returns JSON response with search results based on provided criteria.

**Query Parameters:**
- `name` (optional): Movie name (partial match, case-insensitive)
- `id` (optional): Movie ID (exact match)
- `genre` (optional): Movie genre (partial match, case-insensitive)
- `pirate` (optional): Enable pirate language mode (`true` or `1`)

**Note:** At least one search parameter must be provided.

**Examples:**

Search by name:
```
GET /movies/search?name=prison
```

Search by genre:
```
GET /movies/search?genre=drama
```

Search with multiple criteria:
```
GET /movies/search?name=the&genre=action
```

Search with pirate mode:
```
GET /movies/search?name=treasure&pirate=true
```

**Response Format:**
```json
{
  "success": true,
  "message": "Found 2 movie(s) matching: name: the, genre: action",
  "count": 2,
  "movies": [
    {
      "id": 3,
      "movieName": "The Masked Hero",
      "director": "Chris Moviemaker",
      "year": 2008,
      "genre": "Action/Crime",
      "description": "When a menacing villain wreaks havoc...",
      "duration": 152,
      "imdbRating": 5.0
    }
  ],
  "pirateMode": false
}
```

**Pirate Mode Response:**
```json
{
  "success": true,
  "message": "Ahoy matey! Discovered 1 treasure in yer bounty hunt: name: treasure Arrr!",
  "count": 1,
  "movies": [
    {
      "id": 1,
      "movieName": "The Prison Escape",
      "director": "John captain",
      "year": 1994,
      "genre": "type of adventure",
      "description": "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.",
      "duration": 142,
      "imdbRating": 5.0
    }
  ],
  "pirateMode": true
}
```

### Search Movies (HTML Form)
```
GET /movies/search/form
```
Handles HTML form submissions and returns the movies page with search results.

**Query Parameters:** Same as REST API endpoint

**Example:**
```
GET /movies/search/form?name=prison&pirate=true
```

## Pirate Language Mode 🏴‍☠️

The application includes a fun pirate language feature that can be activated in two ways:

1. **Explicit Parameter**: Add `pirate=true` to any search request
2. **Keyword Detection**: Include pirate-related keywords in your search:
   - "pirate"
   - "arrr"
   - "ahoy"
   - "treasure"

When pirate mode is active:
- Movie descriptions and metadata are translated to pirate language
- UI text changes to pirate terminology
- Response messages include pirate greetings and phrases
- Search results show "treasures" instead of "movies"

**Pirate Language Translations:**
- movie → treasure
- director → captain
- genre → type of adventure
- search → hunt
- results → bounty
- And many more!

## Search Functionality

### Web Interface
The main movies page (`/movies`) includes a comprehensive search form with:
- **Movie Name**: Partial text matching (case-insensitive)
- **Movie ID**: Exact numeric matching
- **Genre**: Partial text matching (case-insensitive)
- **Pirate Mode**: Checkbox to enable pirate language

### Search Features
- **Flexible Criteria**: Search by any combination of name, ID, and genre
- **Partial Matching**: Name and genre searches support substring matching
- **Case Insensitive**: All text searches ignore case
- **Input Validation**: Proper validation with helpful error messages
- **Empty Results Handling**: User-friendly messages when no movies match
- **Real-time Feedback**: Immediate search results display

### Error Handling
- **Invalid Parameters**: Clear error messages for missing or invalid input
- **No Results**: Helpful suggestions when searches return empty results
- **Server Errors**: Graceful handling of unexpected errors
- **Pirate Mode Errors**: Error messages translated to pirate language when appropriate

## Building for Production

```bash
mvn clean package
java -jar target/sample-qdev-movies-0.1.0.jar
```

## Project Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/amazonaws/samples/qdevmovies/
│   │       ├── movies/
│   │       │   ├── MoviesApplication.java    # Main Spring Boot application
│   │       │   ├── MoviesController.java     # REST controller with search endpoints
│   │       │   ├── MovieService.java         # Business logic with search methods
│   │       │   ├── Movie.java                # Movie data model
│   │       │   ├── Review.java               # Review data model
│   │       │   └── ReviewService.java        # Review business logic
│   │       └── utils/
│   │           ├── MovieIconUtils.java       # Movie icon utilities
│   │           ├── MovieUtils.java           # Movie validation utilities
│   │           └── PirateLanguageUtils.java  # Pirate language translation
│   └── resources/
│       ├── application.yml                   # Application configuration
│       ├── movies.json                       # Movie data
│       ├── mock-reviews.json                 # Mock review data
│       ├── log4j2.xml                        # Logging configuration
│       └── templates/
│           ├── movies.html                   # Main page with search form
│           └── movie-details.html            # Movie details page
└── test/                                     # Comprehensive unit tests
    └── java/
        └── com/amazonaws/samples/qdevmovies/
            ├── movies/
            │   ├── MovieServiceTest.java         # Service layer tests
            │   ├── MoviesControllerTest.java     # Original controller tests
            │   └── MoviesControllerSearchTest.java # Search functionality tests
            └── utils/
                └── PirateLanguageUtilsTest.java  # Pirate language tests
```

## Testing

Run all tests:
```bash
mvn test
```

Run specific test classes:
```bash
mvn test -Dtest=MovieServiceTest
mvn test -Dtest=PirateLanguageUtilsTest
mvn test -Dtest=MoviesControllerSearchTest
```

The test suite includes:
- **Unit Tests**: All service methods and utilities
- **Integration Tests**: Controller endpoints with mock services
- **Edge Case Tests**: Invalid parameters, empty results, error conditions
- **Pirate Mode Tests**: Language translation and activation logic

## Usage Examples

### Basic Search Examples

Search for movies with "prison" in the name:
```bash
curl "http://localhost:8080/movies/search?name=prison"
```

Search for drama movies:
```bash
curl "http://localhost:8080/movies/search?genre=drama"
```

Search for a specific movie by ID:
```bash
curl "http://localhost:8080/movies/search?id=1"
```

### Advanced Search Examples

Search with multiple criteria:
```bash
curl "http://localhost:8080/movies/search?name=the&genre=action&pirate=true"
```

Search with pirate mode using keywords:
```bash
curl "http://localhost:8080/movies/search?name=treasure"
```

### HTML Form Usage

Visit `http://localhost:8080/movies` and use the search form:
1. Enter search criteria in any combination of fields
2. Check "Pirate Mode" for fun pirate language
3. Click "Search Movies" or "Hunt for Treasures" (in pirate mode)
4. View results with option to return to all movies

## Troubleshooting

### Port 8080 already in use

Run on a different port:
```bash
mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8081
```

### Build failures

Clean and rebuild:
```bash
mvn clean compile
```

### Search not working

1. Ensure at least one search parameter is provided
2. Check that the movie service is properly loaded
3. Verify the movies.json file is in the resources directory
4. Check application logs for any errors

### Pirate mode not activating

1. Verify the `pirate=true` parameter is included
2. Check for pirate keywords in search text
3. Ensure PirateLanguageUtils is properly configured

## Contributing

This project demonstrates modern Spring Boot development practices. Feel free to:
- Add more movies to the catalog
- Enhance the search functionality
- Improve the pirate language translations
- Add new search criteria (year, rating, etc.)
- Enhance the UI/UX
- Add pagination for large result sets
- Implement caching for better performance

## License

This sample code is licensed under the MIT-0 License. See the LICENSE file.
