package com.movieflix.movieAPI.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileServiceImpl implements FileService{
    @Override
    public String uploadFile(String path, MultipartFile file) throws IOException {
        //get filenname
        String filename = file.getOriginalFilename();

        //get filepath
        String filePath = path + File.separator + filename;

        //create file object
        File file1 = new File(filePath);
        if(!file1.exists()) {
            file1.mkdirs();
        }

        //copy the file to the path
        Files.copy(file.getInputStream(), Paths.get(filePath), StandardCopyOption.REPLACE_EXISTING);

        return filename;
    }

    @Override
    public InputStream getResource(String path, String name) throws FileNotFoundException {
        String filepath = path + File.separator + name;
        return new FileInputStream(filepath);
    }
}
