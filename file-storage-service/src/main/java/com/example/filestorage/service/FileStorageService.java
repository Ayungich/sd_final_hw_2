package com.example.filestorage.service;

import com.example.filestorage.entity.StoredFile;
import com.example.filestorage.repository.StoredFileRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Service
public class FileStorageService {

    private final StoredFileRepository repository;
    private final Path root = Path.of(("/tmp/file-storage"));

    public FileStorageService(StoredFileRepository repository) throws IOException {
        this.repository = repository;
        Files.createDirectories(root);
    }

    public StoredFile store(MultipartFile file) throws IOException {
        String filename = UUID.randomUUID() + "-" + FilenameUtils.getName(file.getOriginalFilename());
        Path location = root.resolve(filename);
        Files.copy(file.getInputStream(), location);

        StoredFile stored = new StoredFile(file.getOriginalFilename(), location.toString(), file.getContentType());
        return repository.save(stored);
    }

    public byte[] load(UUID id) throws IOException {
        StoredFile stored = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("File not found"));
        return Files.readAllBytes(Path.of(stored.getLocation()));
    }
}
