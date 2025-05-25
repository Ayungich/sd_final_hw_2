package com.example.fileanalysis.service;

import com.example.fileanalysis.dto.AnalysisDTO;
import com.example.fileanalysis.entity.FileAnalysis;
import com.example.fileanalysis.repository.FileAnalysisRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

@Service
public class AnalysisService {

    private final FileAnalysisRepository repository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /** Базовый URL File-Storage; по-умолчанию локальный */
    @Value("${storage.base-url:http://localhost:8081}")
    private String storageBaseUrl;

    public AnalysisService(FileAnalysisRepository repository) {
        this.repository = repository;
    }

    @PostConstruct
    void logConfig() {
        System.out.println("storage.base-url = " + storageBaseUrl);
    }

    /* -------------------- Публичное API -------------------- */

    public AnalysisDTO analyze(UUID fileId, byte[] content) {
        String text = new String(content, StandardCharsets.UTF_8);

        long paragraphs = text.split("\\R{2,}").length;
        long words      = text.trim().isEmpty() ? 0 : text.split("\\s+").length;
        long characters = text.length();

        byte[] pngBytes = fetchWordCloudPng(text);
        UUID   cloudId = pngBytes != null ? uploadWordCloud(pngBytes) : null;

        FileAnalysis entity = new FileAnalysis(
                fileId, paragraphs, words, characters, cloudId);
        repository.save(entity);

        return toDTO(entity);
    }

    public Optional<AnalysisDTO> getByFileId(UUID fileId) {
        return repository.findByFileId(fileId).map(this::toDTO);
    }

    public Optional<AnalysisDTO> getByAnalysisId(UUID analysisId) {
        return repository.findById(analysisId).map(this::toDTO);
    }

    /* -------------------- Внутренние -------------------- */

    /**
     * Шаг ①: строим JSON через Jackson и POSTим на quickchart.io/wordcloud
     */
    private byte[] fetchWordCloudPng(String text) {
        try {
            // 1) Собираем JSON-объект
            ObjectNode root = objectMapper.createObjectNode();
            root.put("format", "png");
            root.put("width", 400);
            root.put("height", 300);
            root.put("text", text);

            String payload = objectMapper.writeValueAsString(root);
            System.out.println("QuickChart payload = " + payload);

            // 2) Отправляем
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> req = new HttpEntity<>(payload, headers);

            ResponseEntity<byte[]> res = restTemplate.postForEntity(
                    "https://quickchart.io/wordcloud", req, byte[].class);

            System.out.println("QuickChart status = " + res.getStatusCode());
            System.out.println("QuickChart bytes  = " +
                    (res.getBody() == null ? 0 : res.getBody().length));

            return res.getStatusCode().is2xxSuccessful() ? res.getBody() : null;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        } catch (Exception ex) {
            ex.printStackTrace();  // ошибки сети, timeouts и т.п.
            return null;
        }
    }

    /**
     * Шаг ②: загружаем PNG в File-Storage через multipart/form-data
     */
    private UUID uploadWordCloud(byte[] bytes) {
        try {
            // Часть с ресурсом и явным image/png
            ByteArrayResource resource = new ByteArrayResource(bytes) {
                @Override public String getFilename() { return "cloud.png"; }
            };
            HttpHeaders partHeaders = new HttpHeaders();
            partHeaders.setContentType(MediaType.IMAGE_PNG);
            HttpEntity<ByteArrayResource> part = new HttpEntity<>(resource, partHeaders);

            // Собираем тело multipart
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("file", part);

            HttpHeaders reqHeaders = new HttpHeaders();
            reqHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
            HttpEntity<MultiValueMap<String, Object>> req = new HttpEntity<>(body, reqHeaders);

            ResponseEntity<StoredFileDTO> res = restTemplate.postForEntity(
                    storageBaseUrl + "/files", req, StoredFileDTO.class);

            System.out.println("Upload status = " + res.getStatusCode());
            System.out.println("Upload body   = " + res.getBody());

            return (res.getStatusCode().is2xxSuccessful() && res.getBody() != null)
                    ? res.getBody().id()
                    : null;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    /* -------------------- Утилиты -------------------- */

    private AnalysisDTO toDTO(FileAnalysis a) {
        return new AnalysisDTO(
                a.getId(), a.getFileId(),
                a.getParagraphs(), a.getWords(),
                a.getCharacters(), a.getWordCloudFileId());
    }

    // Вспомогательный DTO для разбора JSON-ответа File-Storage
    private record StoredFileDTO(UUID id, String originalName, String location, String mimeType) {}
}
