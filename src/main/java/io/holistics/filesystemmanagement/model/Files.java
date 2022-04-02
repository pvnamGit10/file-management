package io.holistics.filesystemmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Files extends BaseModel {

    private String fileName;

    private String filePath;

    private String parentFolderPath;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    private String ownerEmail;

    private long size;

    @ManyToOne(fetch = FetchType.LAZY)
    private Folders folders;

    private String content;
}
