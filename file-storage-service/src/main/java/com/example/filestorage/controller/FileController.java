package com.example.filestorage.controller;

import com.example.filestorage.service.FileStorageService;
import com.example.filestorage.entity.StoredFile;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService storageService;

    public FileController(FileStorageService storageService) {
        this.storageService = storageService;
    }

    @Operation(summary = "Upload a file (txt or generated png)")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StoredFile upload(@RequestPart("file") @NotNull MultipartFile file) throws IOException {
        return storageService.store(file);
    }

    @Operation(summary = "Download a stored file by id")
    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable("id") UUID id) throws IOException {
        byte[] content = storageService.load(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .body(content);
    }
}
