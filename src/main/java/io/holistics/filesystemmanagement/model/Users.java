package io.holistics.filesystemmanagement.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Entity;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
public class Users extends BaseModel {
    private String name;
    private String email;
    private String password;
    private Roles roles = Roles.USER;
}
