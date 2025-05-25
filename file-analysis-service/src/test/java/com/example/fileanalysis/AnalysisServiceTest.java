package com.example.fileanalysis;

import com.example.fileanalysis.service.AnalysisService;
import com.example.fileanalysis.repository.FileAnalysisRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;

class AnalysisServiceTest {

    @Test
    void simpleCounts() throws Exception {
        FileAnalysisRepository repo = Mockito.mock(FileAnalysisRepository.class);
        AnalysisService svc = new AnalysisService(repo);

        String text = "Hello world.\n\nSecond line.";
        var dto = svc.analyze(java.util.UUID.randomUUID(), text.getBytes());

        assertEquals(2, dto.paragraphs());
        assertEquals(3, dto.words());
        assertEquals(text.length(), dto.characters());
    }
}
