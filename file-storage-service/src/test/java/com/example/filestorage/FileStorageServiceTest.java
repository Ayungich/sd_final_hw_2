package com.example.filestorage;

import com.example.filestorage.service.FileStorageService;
import com.example.filestorage.repository.StoredFileRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceTest {

    @Test
    void storesFile() throws Exception {
        StoredFileRepository repo = Mockito.mock(StoredFileRepository.class);
        FileStorageService svc = new FileStorageService(repo);

        MockMultipartFile mf = new MockMultipartFile("file", "t.txt", "text/plain", "abc".getBytes(StandardCharsets.UTF_8));
        svc.store(mf);

        Mockito.verify(repo, Mockito.times(1)).save(Mockito.any());
    }
}
