package com.movieflix.movieAPI.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MovieNotFoundException.class)
    public ProblemDetail handleMovieNotFoundException(MovieNotFoundException mnfe) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, mnfe.getMessage());
    }

    @ExceptionHandler(FileExistException.class)
    public ProblemDetail handleFileExistException(FileExistException fee) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, fee.getMessage());
    }

    @ExceptionHandler(EmptyFileException.class)
    public ProblemDetail handleMovieNotFoundException(EmptyFileException efe) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, efe.getMessage());
    }

}
