package com.example.fileanalysis.repository;

import com.example.fileanalysis.entity.FileAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface FileAnalysisRepository extends JpaRepository<FileAnalysis, UUID> {
    Optional<FileAnalysis> findByFileId(UUID fileId);
}
