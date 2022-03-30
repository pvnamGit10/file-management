package io.holistics.filesystemmanagement.payload.response.viewResponse;

import io.holistics.filesystemmanagement.model.Files;
import io.holistics.filesystemmanagement.model.Folders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FolderResponse {
    private String folderName;
    private String folderPath;
    private String parentPath;
    private String size = "0kb";
    private List<Files> files;
    private LocalDateTime createAt;

    public FolderResponse(Folders folder) {
        this.createAt = folder.getCreateAt();
        this.folderName = folder.getFolderName();
        this.folderPath = folder.getFolderPath();
        this.parentPath = folder.getParentPath();
        this.files = folder.getFiles();
    }
}
