package com.movieflix.movieAPI.service;

import com.movieflix.movieAPI.dto.MovieDTO;
import com.movieflix.movieAPI.models.Movie;
import com.movieflix.movieAPI.repository.MovieRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        Movie movie = movieRepository.findById(movieId).orElseThrow(() -> new RuntimeException("Movie not found!"));

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
}
