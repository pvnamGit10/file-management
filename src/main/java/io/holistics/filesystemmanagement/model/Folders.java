package io.holistics.filesystemmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Folders extends BaseModel {
    private String folderName;

    private String folderPath;

    private String parentPath;

    @ManyToOne(fetch = FetchType.LAZY)
    private Users users;

    private String ownerEmail;

    @Transient
    @OneToMany(mappedBy = "folders")
    private List<Files> files;

    @OneToMany
    private List<Folders> subFolders;
}
