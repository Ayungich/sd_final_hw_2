package com.example.filestorage.entity;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String originalName;
    private String location;
    private String mimeType;

    public StoredFile() {}

    public StoredFile(String originalName, String location, String mimeType) {
        this.originalName = originalName;
        this.location = location;
        this.mimeType = mimeType;
    }

    public UUID getId() { return id; }
    public String getOriginalName() { return originalName; }
    public String getLocation() { return location; }
    public String getMimeType() { return mimeType; }
}
