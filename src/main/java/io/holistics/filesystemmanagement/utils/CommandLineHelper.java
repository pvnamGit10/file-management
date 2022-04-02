package io.holistics.filesystemmanagement.utils;

import org.springframework.stereotype.Service;

@Service
public class CommandLineHelper {

    public String standardizeString(String content) {
        content = content.trim();
        content = content.replace("  ", " ");
        return content;
    }

    public String getPrefixCommand(String commandLine) {
        String standardizeCommand = standardizeString(commandLine);
        String prefix = standardizeCommand.split(" ")[0];
        return prefix;
    }

    public String[] separateString(String commandLine) {
        String standardizeCommand = standardizeString(commandLine);
        String[] separated = standardizeCommand.split(" `");
        for (int i = 0; i<separated.length ; i++){
            String newString = separated[i].replace("`","");
            separated[i] = newString;
        }
        return separated;
    }

    public String getDataOfFile(String commandLine) {
        String standardizeCommand = standardizeString(commandLine);
        if (!standardizeCommand.contains("[")) {
            return "";
        }
        try {
            String data = standardizeCommand.substring(
                    standardizeCommand.indexOf("[") + 1, standardizeCommand.lastIndexOf("]")
            );
            return data;
        } catch (Exception e) {
            return "";
        }
    }

    public String getFileOrFolderPath(String commandLine) {
        String[] command = separateString(commandLine);
        try {
            String data = "";
            if (command.length == 2) {
                data = command[1];
            } else {
                if (command[2].contains("[")) {
                    data = command[1];
                } else {
                    data = command[2];
                }
            }
            return data;
        } catch (Exception e) {
            return "";
        }
    }

    public String getFolderName(String filePath) {
        String fileName = "";
        if (filePath.contains("/")) {
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        } else {
            fileName = filePath;
        }
        return fileName;
    }

    public String getFileName(String filePath) {
        String fileName = "";
        if (filePath.contains("/")) {
            fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
        } else {
            fileName = filePath;
        }
        if (!(fileName.substring(fileName.lastIndexOf(".")).equals(".txt")))
            throw new IllegalArgumentException("Exception on get file name: file name must have .txt");
        return fileName;
    }

    public String getFolderParentPath(String path) {
        String folderPath = "";
        if (path.contains("/")) {
            folderPath = path.substring(0, path.lastIndexOf("/"));
        }
        return folderPath;
    }

    public boolean checkCommandLineContainsParentPath(String commandLine) {
        String[] splitCommand = separateString(commandLine);
        return splitCommand.length >= 3 && !splitCommand[2].startsWith("[");
    }

    public String getParentPathForCreate(String commandLine) {
        String[] separatedCommand = separateString(commandLine);
        return separatedCommand[1];
    }
}
