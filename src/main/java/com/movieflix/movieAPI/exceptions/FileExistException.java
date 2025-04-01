package com.movieflix.movieAPI.exceptions;

public class FileExistException extends RuntimeException{
    public FileExistException(String message) {
        super(message);
    }
}
