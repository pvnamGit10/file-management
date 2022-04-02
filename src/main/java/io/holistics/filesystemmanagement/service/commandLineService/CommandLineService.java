package io.holistics.filesystemmanagement.service.commandLineService;

import io.holistics.filesystemmanagement.model.Files;
import io.holistics.filesystemmanagement.model.Folders;
import io.holistics.filesystemmanagement.payload.request.CommandLineRequest;
import io.holistics.filesystemmanagement.payload.response.viewResponse.DataResponse;
import io.holistics.filesystemmanagement.payload.response.viewResponse.FileResponse;
import io.holistics.filesystemmanagement.payload.response.viewResponse.FolderResponse;
import io.holistics.filesystemmanagement.repository.FileRepository;
import io.holistics.filesystemmanagement.repository.FolderRepository;
import io.holistics.filesystemmanagement.utils.CommandLineHelper;
import io.holistics.filesystemmanagement.utils.FilesAndFoldersHelper;
import org.openjdk.jol.vm.VM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class CommandLineService {
    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private CommandLineHelper commandLineHelper;

    @Autowired
    private FilesAndFoldersHelper filesAndFoldersHelper;

    private final String REGEX = "/^[a-zA-Z0-9 _-]+$/";

    @Transactional
    public DataResponse createFileOrFolder(CommandLineRequest body) {
        //get data from command line
        String data = commandLineHelper.getDataOfFile(body.getCommandLine());
        //if data is not empty, create file
        if (!data.isEmpty()) {
            Files file = new Files();

            //get folder path, from begin to previous of file name,
            // example a PATH is root/demo/fileTest => filePath is "root/demo"
            String folderPath = commandLineHelper.getFileOrFolderPath(body.getCommandLine());
            folderPath = addDefaultRoot(folderPath);
            String parentPath = commandLineHelper.getFolderParentPath(folderPath);
            Folders folders = null;
            if (commandLineHelper.checkCommandLineContainsParentPath(body.getCommandLine())) {
                createParentPath(body);
            } else {
                folders = folderRepository.findByFolderPathAndArchivedIsFalse(parentPath).orElseThrow(() -> {
                    throw new IllegalArgumentException("create error: no folder found");
                });
            }
            //throw error when not found file path in folder

            //get file name, example a PATH is root/demo/fileTest => fileName is "fileTest"

            //get file path, from begin to end PATH,
            // example a PATH is root/demo/fileTest => filePath is "root/demo/fileTest"
            String filePath = commandLineHelper.getFileOrFolderPath(body.getCommandLine());
            filePath = addDefaultRoot(filePath);
            if (filesAndFoldersHelper.checkFileIsExisted(filePath)) {
                throw new IllegalArgumentException("create error: file is existed");
            }
            String fileName = commandLineHelper.getFileName(filePath);
            if (!fileName.matches(REGEX)) {
                throw new IllegalArgumentException("create error: file name fail");
            }
            file.setParentFolderPath(parentPath);
            file.setFilePath(filePath);
            file.setFolders(folders);
            file.setFileName(fileName);
            LocalDateTime createAt = LocalDateTime.now();
            file.setCreateAt(createAt);
            file.setContent(data);
            long size = VM.current().sizeOf(file);
            file.setSize(size);
            fileRepository.save(file);
        } else { //if there is no data, create folder instead

            //get folder path, from begin to previous of file name,
            // example a PATH is root/demo/fileTest => filePath is "root/demo"
            String folderPath = commandLineHelper.getFileOrFolderPath(body.getCommandLine());
            folderPath = addDefaultRoot(folderPath);
            if (folderPath.contains(".txt")) {
                throw new IllegalArgumentException("create error: folder does not contains .txt");
            }
            if (filesAndFoldersHelper.checkFolderIsExisted(folderPath)) {
                throw new IllegalArgumentException("create error: folder is existed");
            }

            String parentFolderPath = commandLineHelper.getFolderParentPath(folderPath);

            if (commandLineHelper.checkCommandLineContainsParentPath(body.getCommandLine())) {
                createParentPath(body);
            } else {
                if (!parentFolderPath.substring(parentFolderPath.indexOf("/") + 1).contains("/")) {
                    parentFolderPath = body.getPath();
                }
                folderRepository.findByFolderPathAndArchivedIsFalse(parentFolderPath).orElseThrow(() -> {
                    throw new IllegalArgumentException("create error: : no folder found");
                });
            }


            String folderName = commandLineHelper.getFolderName(folderPath);
            if (!folderName.matches(REGEX)) {
                throw new IllegalArgumentException("create error: folder name fail");
            }
            LocalDateTime createAt = LocalDateTime.now();

            Folders folder = new Folders();
            folder.setFolderName(folderName);
            folder.setCreateAt(createAt);
            folder.setParentPath(parentFolderPath);
            folder.setFolderPath(folderPath);
            folderRepository.save(folder);

            //insert sub folder to parent folder
            Folders parentFolder = folderRepository.findByFolderPathAndArchivedIsFalse(parentFolderPath).orElseThrow(() -> {
                throw new IllegalArgumentException("create error: no parent folder found");
            });
            ArrayList<Folders> listSubFolder = new ArrayList<>();
            listSubFolder.add(folder);
            parentFolder.setSubFolders(listSubFolder);
            folderRepository.save(parentFolder);
        }
        DataResponse response = new DataResponse();
        response.setStatus("success");
        return response;
    }

    public DataResponse displayFile(CommandLineRequest body) {
        String filePath = commandLineHelper.getFileOrFolderPath(body.getCommandLine());
        filePath = addDefaultRoot(filePath);
        Files file = fileRepository.findByFilePathAndArchivedIsFalse(filePath).orElseThrow(
                () -> {
                    throw new IllegalArgumentException("cat error: file not found");
                }
        );
        DataResponse response = new DataResponse();
        response.setContent(file.getContent());
        response.setStatus("success");
        return response;
    }

    public DataResponse removeFileOrFolder(CommandLineRequest body) {
        String[] separatedCommand = commandLineHelper.separateString(body.getCommandLine());

        int numberOfPath = separatedCommand.length;

        while (numberOfPath > 1) {
            String[] paths = separatedCommand;
            String filePath = paths[paths.length - 1];
            filePath = addDefaultRoot(filePath);
            // if file path contains .txt => it is a file
            if (filesAndFoldersHelper.isAFilePath(filePath)) {
                Files file = fileRepository.findByFilePathAndArchivedIsFalse(filePath).orElseThrow(
                        () -> {
                            throw new IllegalArgumentException("remove error: no file found");
                        });
                file.setArchived(true);
                fileRepository.save(file);
            } else {
                Folders folder = folderRepository.findByFolderPathAndArchivedIsFalse(filePath).orElseThrow(
                        () -> {
                            throw new IllegalArgumentException("remove error: No file found");
                        });
                folder.setArchived(true);
                folderRepository.save(folder);
            }

            numberOfPath--;
        }
        DataResponse response = new DataResponse();
        response.setStatus("success");
        return response;
    }

    public DataResponse updateFileOrFolder(CommandLineRequest body) {
        String[] separatedCommand = commandLineHelper.separateString(body.getCommandLine());
        String path = separatedCommand[1];
        path = addDefaultRoot(path);
        if (filesAndFoldersHelper.isAFilePath(path)) {
            String newName = separatedCommand[2];
            if (!newName.matches(REGEX)) {
                throw new IllegalArgumentException("update error: file name fail");
            }
            Files file = fileRepository.findByFilePathAndArchivedIsFalse(path)
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("update error: no file found");
                    });
            String newFilePath = commandLineHelper.getFolderParentPath(path).concat("/").concat(newName);
            newFilePath = addDefaultRoot(newFilePath);
            file.setFilePath(newFilePath);
            file.setFileName(newName);
            String data = commandLineHelper.getDataOfFile(body.getCommandLine());
            if (!data.isEmpty()) {
                file.setContent(data);
            }
            fileRepository.save(file);
        } else {
            String newName = separatedCommand[2];
            if (!newName.matches(REGEX)) {
                throw new IllegalArgumentException("update error: folder name fail");
            }
            Folders folder = folderRepository.findByFolderPathAndArchivedIsFalse(path)
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("update error: no folder found");
                    });
            folder.setFolderName(newName);
            folderRepository.save(folder);

            //Update sub folder in parent folder
            String parentFolderPath = commandLineHelper.getFolderParentPath(path);
            Folders parentFolder = folderRepository.findByFolderPathAndArchivedIsFalse(parentFolderPath).orElseThrow(() -> {
                throw new IllegalArgumentException("update error: no parent folder found");
            });
            ArrayList<Folders> listSubFolder = new ArrayList<>();
            listSubFolder.add(folder);
            parentFolder.setSubFolders(listSubFolder);
            folderRepository.save(parentFolder);
        }
        DataResponse response = new DataResponse();
        response.setStatus("success");
        return response;
    }

    public DataResponse moveFileOrFolder(CommandLineRequest body) {
        String[] separatedCommand = commandLineHelper.separateString(body.getCommandLine());
        String oldPath = separatedCommand[1];
        oldPath = addDefaultRoot(oldPath);
        String newPath = separatedCommand[2];
        newPath = addDefaultRoot(newPath);
        folderRepository.findByFolderPathAndArchivedIsFalse(newPath).orElseThrow(() -> {
            throw new IllegalArgumentException("move error: new folder not existed");
        });

        if (newPath.contains(oldPath)) {
            throw new IllegalArgumentException("move error: new folder is subfolder");
        }

        if (filesAndFoldersHelper.checkFileIsExisted(oldPath) && filesAndFoldersHelper.isAFilePath(oldPath)) {
            String fileName = commandLineHelper.getFileName(oldPath);
            if (!fileName.matches(REGEX)) {
                throw new IllegalArgumentException("move error: file name fail");
            }
            Files file = fileRepository.findByFilePathAndArchivedIsFalse(oldPath).orElseThrow(
                    () -> {
                        throw new IllegalArgumentException("move error: file not found");
                    }
            );
            file.setParentFolderPath(newPath);
            file.setFilePath(newPath.concat("/").concat(fileName));
            fileRepository.save(file);
            DataResponse response = new DataResponse();
            response.setStatus("success");
            return response;
        } else if (filesAndFoldersHelper.checkFolderIsExisted(oldPath)) {
            String folderName = commandLineHelper.getFileName(oldPath);
            if (!folderName.matches(REGEX)) {
                throw new IllegalArgumentException("move error: folder name fail");
            }
            Folders folder = folderRepository.findByFolderPathAndArchivedIsFalse(oldPath)
                    .orElseThrow(
                            () -> {
                                throw new IllegalArgumentException("move error: folder not found");
                            }
                    );
            ;
            folder.setFolderPath(newPath.concat("/").concat(folderName));
            folder.setParentPath(newPath);
            folderRepository.save(folder);

            //update sub folder in parent folder
            String parentFolderPath = commandLineHelper.getFolderParentPath(newPath);
            Folders parentFolder = folderRepository.findByFolderPathAndArchivedIsFalse(parentFolderPath).orElseThrow(() -> {
                throw new IllegalArgumentException("move error: no parent folder found");
            });
            ArrayList<Folders> listSubFolder = new ArrayList<>();
            listSubFolder.add(folder);
            parentFolder.setSubFolders(listSubFolder);
            folderRepository.save(parentFolder);
            DataResponse response = new DataResponse();
            response.setStatus("success");
            return response;
        } else {
            throw new IllegalArgumentException("move error: : no element found");
        }
    }

    public DataResponse changeDirect(CommandLineRequest body) {
        String[] separatedCommand = commandLineHelper.separateString(body.getCommandLine());
        String path = separatedCommand[1];
        path = addDefaultRoot(path);
        folderRepository.findByFolderPathAndArchivedIsFalse(path)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("error: not found folder");
                });
        DataResponse response = new DataResponse();
        response.setContent(path);
        response.setStatus("success");
        return response;
    }

    public DataResponse search(CommandLineRequest body) {
        try {
            String[] separatedCommand = commandLineHelper.separateString(body.getCommandLine());
            String name = separatedCommand[1];
            boolean hadFolderPath = separatedCommand.length > 2;
            if (hadFolderPath) {
                String folderPath = separatedCommand[2];
                folderPath = addDefaultRoot(folderPath);

                // get list of files
                List<Files> listOfFiles = fileRepository
                        .findByNameAndFolder(name, folderPath)
                        .orElseThrow(() -> {
                            throw new IllegalArgumentException("find error: no file found");
                        });
                // convert original list to response
                ArrayList<FileResponse> fileResponses = new ArrayList<>();
                listOfFiles.forEach(file -> {
                    FileResponse fileResponse = new FileResponse(file);
                    long size = VM.current().sizeOf(file);
                    fileResponse.setSize(size + " bytes");
                    fileResponses.add(fileResponse);
                });

                // get list of folders
                List<Folders> listOfFolders = folderRepository
                        .findByNameAndFolderParent(name, folderPath)
                        .orElseThrow(() -> {
                            throw new IllegalArgumentException("find error: no folder found");
                        });

                if (listOfFiles.isEmpty() && listOfFolders.isEmpty()) {
                    DataResponse dataResponse = new DataResponse();
                    dataResponse.setContent("no result");
                    dataResponse.setStatus("success");
                    return dataResponse;
                }
                // convert original list to response
                ArrayList<FolderResponse> folderResponses = new ArrayList<>();
                listOfFolders.forEach(folder -> {
                    FolderResponse response = new FolderResponse(folder);
                    folderResponses.add(response);
                });

                //return view
                DataResponse dataResponse = new DataResponse();
                dataResponse.setFiles(fileResponses);
                dataResponse.setFolders(folderResponses);
                return dataResponse;
            } else {
                List<Files> listOfFiles = fileRepository
                        .findByName(name)
                        .orElseThrow(() -> {
                            throw new IllegalArgumentException("find error: no file found");
                        });

                // convert original list to response
                ArrayList<FileResponse> fileResponses = new ArrayList<>();
                if (listOfFiles.size() > 0)
                    for (Files file : listOfFiles) {
                        FileResponse fileResponse = new FileResponse(file);
                        fileResponses.add(fileResponse);
                    }


                // get list of folders
                List<Folders> listOfFolders = folderRepository
                        .findByName(name)
                        .orElseThrow(() -> {
                            throw new IllegalArgumentException("find error: no folder found");
                        });
                if (listOfFiles.isEmpty() && listOfFolders.isEmpty()) {
                    DataResponse dataResponse = new DataResponse();
                    dataResponse.setContent("no result");
                    dataResponse.setStatus("success");
                    return dataResponse;
                }

                // convert original list to response
                ArrayList<FolderResponse> folderResponses = new ArrayList<>();
                if (listOfFolders.size() > 0)
                    for (Folders folder : listOfFolders) {
                        FolderResponse response = new FolderResponse(folder);
                        response.setSize(getSizeFolder(folder) + (getSizeFolder(folder) > 0 ? " bytes" : " byte"));
                        folderResponses.add(response);
                    }

                // return view
                DataResponse dataResponse = new DataResponse();
                dataResponse.setFiles(fileResponses);
                dataResponse.setFolders(folderResponses);
                dataResponse.setStatus("success");
                return dataResponse;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public DataResponse displayFilesAndFolder(CommandLineRequest body) {
        try {
            String[] separatedCommand = commandLineHelper.separateString(body.getCommandLine());
            boolean hadFolderPath = separatedCommand.length >= 2;
            String path = "";
            if (hadFolderPath) {
                path = separatedCommand[1]; // use [FOLDER_PATH]
                path = addDefaultRoot(path);
            } else {
                path = body.getPath(); //use path from body request
            }
            // get list of files
            List<Files> listOfFiles = fileRepository
                    .findByFolderPath(path)
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("ls error: no file found");
                    });
            // convert original list to response
            ArrayList<FileResponse> fileResponses = new ArrayList<>();
            if (listOfFiles.size() > 0)
                for (Files file : listOfFiles) {
                    FileResponse fileResponse = new FileResponse(file);
                    fileResponses.add(fileResponse);
                }

            // get list of folders
            List<Folders> listOfFolders = folderRepository
                    .findByFolderParentPath(path)
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("ls error: no folder found");
                    });

            // convert original list to response
            ArrayList<FolderResponse> folderResponses = new ArrayList<>();
            if (listOfFolders.size() > 0)
                for (Folders folder : listOfFolders) {
                    FolderResponse response = new FolderResponse(folder);
                    response.setSize(getSizeFolder(folder) + (getSizeFolder(folder) > 0 ? " bytes" : " byte"));
                    folderResponses.add(response);
                }

            DataResponse dataResponse = new DataResponse();
            dataResponse.setFiles(fileResponses);
            dataResponse.setFolders(folderResponses);
            dataResponse.setStatus("success");
            return dataResponse; // use fo response API

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void createParentPath(CommandLineRequest body) {
        // Create folder if command line has [-p] parameter and is not exist yet
        // if the command is cr [-p] PATH [DATA], get [-p] as a parentPathForCreate
        String parentPathForCreate = commandLineHelper.getParentPathForCreate(body.getCommandLine());
        parentPathForCreate = addDefaultRoot(parentPathForCreate);

        //get parent path from parentPathForCreate
        String parentFolderPath = commandLineHelper.getFolderParentPath(parentPathForCreate);

        //if parentPathForCreate is not existed, create it
        if (folderRepository.findByFolderPathAndArchivedIsFalse(parentPathForCreate).isEmpty()) {
            if (parentFolderPath.contains(".txt")) {
                throw new IllegalArgumentException("create error: folder does not contains .txt");
            }
            Folders folder = new Folders();
            LocalDateTime createAt = LocalDateTime.now();
            folder.setCreateAt(createAt);
            folder.setFolderPath(parentPathForCreate);
            folder.setParentPath(parentFolderPath);
            folderRepository.save(folder);
        }
    }

    private long getSizeFolder(Folders folder) {
        long size = 0;
        List<Files> files = fileRepository.findByFolderPath(folder.getFolderPath()).get();
        if (!files.isEmpty()) {
            for (Files file : files) {
                size += file.getSize();
            }
        }
        return size;
    }

    private String addDefaultRoot(String path) {
        return "root/" + path;
    }
}
