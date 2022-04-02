package io.holistics.filesystemmanagement;

import io.holistics.filesystemmanagement.model.Folders;
import io.holistics.filesystemmanagement.repository.FolderRepository;
import org.openjdk.jol.vm.VM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class FileSystemManagementApplication {

    @Autowired
    private FolderRepository folderRepository;

    public static void main(String[] args) {
        SpringApplication.run(FileSystemManagementApplication.class, args);
    }


    @Bean
    ApplicationRunner appStarted() {
        return args -> {
            String root = "root";
            boolean hadRootFolder = folderRepository.findByFolderPathAndArchivedIsFalse(root).isPresent();
            if(!hadRootFolder) {
                Folders folder = new Folders();
                LocalDateTime createAt = LocalDateTime.now();
                folder.setCreateAt(createAt);
                folder.setFolderPath(root);
                folder.setFolderName(root);
                folderRepository.save(folder);
            }
        };
    }

}
