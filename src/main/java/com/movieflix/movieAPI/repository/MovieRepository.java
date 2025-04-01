package com.movieflix.movieAPI.repository;

import com.movieflix.movieAPI.models.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovieRepository extends JpaRepository<Movie, Long> {
}
