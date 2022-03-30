package io.holistics.filesystemmanagement.repository;

import io.holistics.filesystemmanagement.model.Folders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FolderRepository extends JpaRepository<Folders, Long> {

    Optional<Folders> findByFolderPathAndArchivedIsFalse(String path);

    Optional<Folders> findByParentPathAndArchivedIsFalse(String path);

    @Query("SELECT f FROM Folders f where f.archived = false and f.folderName like %?1% and f.parentPath = ?2")
    Optional<List<Folders>> findByNameAndFolderParent(String fileName, String path);

    @Query("SELECT f FROM Folders f where f.archived = false and f.parentPath like %?1%")
    Optional<List<Folders>> findByFolderParentPath(String path);

    @Query("SELECT f FROM Folders f where f.archived = false and f.folderName like %?1%")
    Optional<List<Folders>> findByName(String fileName);
}
