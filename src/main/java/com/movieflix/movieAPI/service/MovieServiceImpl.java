package com.movieflix.movieAPI.service;

import com.movieflix.movieAPI.dto.MovieDTO;
import com.movieflix.movieAPI.exceptions.EmptyFileException;
import com.movieflix.movieAPI.exceptions.FileExistException;
import com.movieflix.movieAPI.exceptions.MovieNotFoundException;
import com.movieflix.movieAPI.models.Movie;
import com.movieflix.movieAPI.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Service
public class MovieServiceImpl implements MovieService{
    
    private final MovieRepository movieRepository;
    private final FileService fileService;

    @Value("${file.upload-dir}")
    private String path;

    @Value("${url}")
    private String url;

    public MovieServiceImpl(MovieRepository movieRepository, FileService fileService) {
        this.movieRepository = movieRepository;
        this.fileService = fileService;
    }


    @Override
    public MovieDTO addMovie(MovieDTO movieDTO, MultipartFile file) throws IOException {
        //1. uploading the file
        if(Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
            throw new EmptyFileException("File already exist! Please enter another file.");
        }
        String uploadedFileName = fileService.uploadFile(path, file);

        //2. set the value of field "poster" as file name
        movieDTO.setPoster(uploadedFileName);


        //3. map dto into Movie object
        Movie movie  = new Movie(
                null,
                movieDTO.getTitle(),
                movieDTO.getDirector(),
                movieDTO.getStudio(),
                movieDTO.getMovieCast(),
                movieDTO.getReleaseYear(),
                movieDTO.getPoster()
        );

        //4. save the movie object -> saved movie object
        movieRepository.save(movie);

        //5. generate the poster url
        String posterUrl = url + "/file/" + uploadedFileName;

        //6. map movie object to DTO object and return it
        MovieDTO responseMovie = new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );
        return responseMovie;
    }

    @Override
    public MovieDTO getMovie(Long movieId) {
        //1. Search for the movie if exist
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId + "!"));

        //2. Generate URL
        String posterUrl = url + "/file/" + movie.getPoster();

        //3. Map to movieDTO
        MovieDTO responseMovie = new MovieDTO(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster(),
                posterUrl
        );

        return responseMovie;
    }

    @Override
    public List<MovieDTO> getAllMovies() {
        //1. Fetch all movies
        List<Movie> movies = movieRepository.findAll();

        //2. Iterate or map through the list and generate urls for each movie and map to each movieDTO objects
        List<MovieDTO> movieDTOs = new ArrayList<>();
        for (Movie movie: movies) {
            String posterUrl = url + "/file/" + movie.getPoster();
            MovieDTO movieDTO = new MovieDTO(
                    movie.getMovieId(),
                    movie.getTitle(),
                    movie.getDirector(),
                    movie.getStudio(),
                    movie.getMovieCast(),
                    movie.getReleaseYear(),
                    movie.getPoster(),
                    posterUrl
            );
            movieDTOs.add(movieDTO);
        }

        return movieDTOs;
    }

    @Override
    public MovieDTO updateMovie(Long movieId, MovieDTO movieDTO, MultipartFile file) throws IOException {
        //1. Check if movie exist with given movie ID.
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId + "!"));


        //2. If file is null, do nothing
        //if file is not null, then delete existing file with the record and upload new file
        String filename = movie.getPoster();
        if(file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + filename));
            filename = fileService.uploadFile(path, file);
        }

        //3. Set movie poster value, according to step 2
        movie.setTitle(movieDTO.getTitle());
        movie.setDirector(movieDTO.getDirector());
        movie.setStudio(movieDTO.getStudio());
        movie.setMovieCast(movieDTO.getMovieCast());
        movie.setReleaseYear(movieDTO.getReleaseYear());
        movie.setPoster(filename);

        //4. Map to movie object
        Movie updatedMovie = new Movie(
                movie.getMovieId(),
                movie.getTitle(),
                movie.getDirector(),
                movie.getStudio(),
                movie.getMovieCast(),
                movie.getReleaseYear(),
                movie.getPoster()
        );

        //5. Save the movie object -> return the saved movie object
        Movie umv = movieRepository.save(updatedMovie);

        //6. generate posterUrl for it
        String posterUrl = url + "/file/" + umv.getPoster();

        //7. map to MovieDto object and return it.
        MovieDTO responseMovie = new MovieDTO(
                umv.getMovieId(),
                umv.getTitle(),
                umv.getDirector(),
                umv.getStudio(),
                umv.getMovieCast(),
                umv.getReleaseYear(),
                umv.getPoster(),
                posterUrl
        );
        return responseMovie;
    }

    @Override
    public String deleteMovie(Long movieId) throws IOException{
        //1. check if movie exist
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new MovieNotFoundException("Movie not found with id = " + movieId + "!"));

        //2. delete file associated with object
        Files.deleteIfExists(Paths.get(path + File.separator + movie.getPoster()));
        //3. delete the movie from database
        movieRepository.delete(movie);
        return "Movie deleted with id = " + movieId;
    }
}
