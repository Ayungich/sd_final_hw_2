package com.example.fileanalysis.controller;

import com.example.fileanalysis.dto.AnalysisDTO;
import com.example.fileanalysis.service.AnalysisService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisService analysisService;
    private final RestTemplate restTemplate = new RestTemplate();

    /** Базовый URL File-Storage (переопределяем профилем или переменной окружения) */
    @Value("${storage.base-url:http://localhost:8081}")
    private String storageBaseUrl;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    /* ---------- 1. Запустить анализ ---------- */
    @Operation(summary = "Run analysis for stored file")
    @PostMapping(value = "/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public AnalysisDTO analyze(@PathVariable("fileId") @NotNull UUID fileId) throws IOException {
        byte[] content = restTemplate.getForObject(
                storageBaseUrl + "/files/" + fileId, byte[].class);

        return analysisService.analyze(fileId, content);
    }

    /* ---------- 2. Получить анализ по analysisId ---------- */
    @Operation(summary = "Get analysis by analysis id")
    @GetMapping(value = "/by-id/{analysisId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalysisDTO> getById(@PathVariable("analysisId") @NotNull UUID analysisId) {
        return analysisService.getByAnalysisId(analysisId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /* ---------- 3. Получить анализ по fileId ---------- */
    @Operation(summary = "Get analysis by original file id")
    @GetMapping(value = "/by-file/{fileId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalysisDTO> getByFile(@PathVariable("fileId") @NotNull UUID fileId) {
        return analysisService.getByFileId(fileId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
