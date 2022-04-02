package io.holistics.filesystemmanagement.payload.response.viewResponse;

import io.holistics.filesystemmanagement.model.Files;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FileResponse {
    private String fileName;
    private String filePath;
    private String size = "0 bytes";
    private LocalDateTime createAt;

    public FileResponse(Files file) {
        this.createAt = file.getCreateAt();
        this.fileName = file.getFileName();
        this.filePath = file.getFilePath();
        this.size = file.getSize()+" bytes";
    }
}
