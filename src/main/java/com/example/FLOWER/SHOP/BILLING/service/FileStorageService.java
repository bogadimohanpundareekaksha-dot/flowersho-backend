package com.example.FLOWER.SHOP.BILLING.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadDirectory;

    public FileStorageService(@Value("${file.upload-dir:uploads}") String uploadDir) {
        this.uploadDirectory = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";
        int lastDot = originalName.lastIndexOf('.');
        if (lastDot > 0) {
            extension = originalName.substring(lastDot);
        }

        String filename = UUID.randomUUID() + extension;
        Path destination = uploadDirectory.resolve(filename);

        try {
            Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);
            return filename;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file " + filename, e);
        }
    }

    public Resource loadFileAsResource(String filename) {
        Path filePath = uploadDirectory.resolve(filename).normalize();
        return new FileSystemResource(filePath.toFile());
    }
}
