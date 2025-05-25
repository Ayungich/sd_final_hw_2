package com.example.fileanalysis.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class FileAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private UUID fileId;
    private long paragraphs;
    private long words;
    private long characters;
    private UUID wordCloudFileId;

    public FileAnalysis() {}

    public FileAnalysis(UUID fileId, long paragraphs, long words, long characters, UUID wordCloudFileId) {
        this.fileId = fileId;
        this.paragraphs = paragraphs;
        this.words = words;
        this.characters = characters;
        this.wordCloudFileId = wordCloudFileId;
    }

    public UUID getId() { return id; }
    public UUID getFileId() { return fileId; }
    public long getParagraphs() { return paragraphs; }
    public long getWords() { return words; }
    public long getCharacters() { return characters; }
    public UUID getWordCloudFileId() { return wordCloudFileId; }
}
