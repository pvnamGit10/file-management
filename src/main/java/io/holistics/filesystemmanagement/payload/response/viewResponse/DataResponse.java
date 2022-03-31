package io.holistics.filesystemmanagement.payload.response.viewResponse;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class DataResponse {
    private List<FileResponse> files;
    private List<FolderResponse> folders;
    private String content;
    private String status;
}
