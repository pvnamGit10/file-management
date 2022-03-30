package io.holistics.filesystemmanagement.payload.request;

import lombok.Data;

@Data
public class CommandLineRequest {
    private String commandLine;
    private String path;
}
