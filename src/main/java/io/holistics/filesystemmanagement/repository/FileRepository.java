package io.holistics.filesystemmanagement.repository;

import io.holistics.filesystemmanagement.model.Files;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<Files, Long> {
    Optional<Files> findByFilePathAndArchivedIsFalse(String path);

    @Query("SELECT f FROM Files f where f.archived = false and f.fileName like %?1%")
    Optional<List<Files>> findByName(String fileName);

    @Query("SELECT f FROM Files f where f.archived = false and f.parentFolderPath like %?1%")
    Optional<List<Files>> findByFolderPath(String path);

    @Query("SELECT f FROM Files f where f.archived = false and f.fileName like %?1% and f.parentFolderPath = ?2")
    Optional<List<Files>> findByNameAndFolder(String fileName, String path);
}
