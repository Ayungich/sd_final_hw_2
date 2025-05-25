package com.example.fileanalysis.dto;

import java.util.UUID;

public record AnalysisDTO(UUID analysisId,
                          UUID fileId,
                          long paragraphs,
                          long words,
                          long characters,
                          UUID wordCloudFileId) {
}
