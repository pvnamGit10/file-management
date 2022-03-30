package io.holistics.filesystemmanagement.utils;

import io.holistics.filesystemmanagement.repository.FileRepository;
import io.holistics.filesystemmanagement.repository.FolderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FilesAndFoldersHelper {

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private FileRepository fileRepository;

    public boolean checkFolderIsExisted(String folderPath) {
        return folderRepository.findByFolderPathAndArchivedIsFalse(folderPath).isPresent();
    }

    public boolean checkFileIsExisted(String filePath) {
        return fileRepository.findByFilePathAndArchivedIsFalse(filePath).isPresent();
    }

    public boolean isAFilePath(String filePath) {
        return (filePath.substring(filePath.lastIndexOf(".") + 1).equals(".txt"));
    }
}
