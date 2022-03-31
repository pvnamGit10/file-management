package io.holistics.filesystemmanagement.service.commandLineService;

import io.holistics.filesystemmanagement.model.Files;
import io.holistics.filesystemmanagement.model.Folders;
import io.holistics.filesystemmanagement.payload.request.CommandLineRequest;
import io.holistics.filesystemmanagement.payload.response.viewResponse.FileResponse;
import io.holistics.filesystemmanagement.payload.response.viewResponse.FolderResponse;
import io.holistics.filesystemmanagement.payload.response.viewResponse.ViewResponse;
import io.holistics.filesystemmanagement.repository.FileRepository;
import io.holistics.filesystemmanagement.repository.FolderRepository;
import io.holistics.filesystemmanagement.utils.CommandLineHelper;
import io.holistics.filesystemmanagement.utils.FilesAndFoldersHelper;
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


    @Transactional
    public void createFileOrFolder(CommandLineRequest body) {
        //get data from command line
        String data = commandLineHelper.getDataOfFile(body.getCommandLine());
        //if data is not empty, create file
        if (!data.isEmpty()) {
            Files file = new Files();

            //get folder path, from begin to previous of file name,
            // example a PATH is root/demo/fileTest => filePath is "root/demo"
            String folderPath = commandLineHelper.getFileOrFolderPath(body.getCommandLine());
            String parentPath = commandLineHelper.getFolderParentPath(folderPath);
            Folders folders = null;
            if (commandLineHelper.checkCommandLineContainsParentPath(body.getCommandLine())) {
                createParentPath(body);
            } else {
                folders = folderRepository.findByFolderPathAndArchivedIsFalse(parentPath).orElseThrow(() -> {
                    throw new IllegalArgumentException("Exception in create File: No such folder");
                });
            }
            //throw error when not found file path in folder

            //get file name, example a PATH is root/demo/fileTest => fileName is "fileTest"

            //get file path, from begin to end PATH,
            // example a PATH is root/demo/fileTest => filePath is "root/demo/fileTest"
            String filePath = commandLineHelper.getFileOrFolderPath(body.getCommandLine());
            if (filesAndFoldersHelper.checkFileIsExisted(filePath)) {
                throw new IllegalArgumentException("File is existed");
            }
            String fileName = commandLineHelper.getFileName(filePath);
            file.setParentFolderPath(parentPath);
            file.setFilePath(filePath);
            file.setFolders(folders);
            file.setFileName(fileName);
            LocalDateTime createAt = LocalDateTime.now();
            file.setCreateAt(createAt);
            file.setContent(data);

            fileRepository.save(file);
        } else { //if there is no data, create folder instead

            //get folder path, from begin to previous of file name,
            // example a PATH is root/demo/fileTest => filePath is "root/demo"
            String folderPath = commandLineHelper.getFileOrFolderPath(body.getCommandLine());
            if (filesAndFoldersHelper.checkFolderIsExisted(folderPath)) {
                throw new IllegalArgumentException("Folder is existed");
            }

            String parentFolderPath = commandLineHelper.getFolderParentPath(folderPath);

            if (commandLineHelper.checkCommandLineContainsParentPath(body.getCommandLine())) {
                createParentPath(body);
            } else {
                folderRepository.findByFolderPathAndArchivedIsFalse(parentFolderPath).orElseThrow(() -> {
                    throw new IllegalArgumentException("Exception in create Folder: No such folder");
                });
            }


            String folderName = commandLineHelper.getFolderName(folderPath);
            LocalDateTime createAt = LocalDateTime.now();

            Folders folder = new Folders();
            folder.setFolderName(folderName);
            folder.setCreateAt(createAt);
            folder.setParentPath(parentFolderPath);
            folder.setFolderPath(folderPath);

            folderRepository.save(folder);

            //insert sub folder to parent folder
            Folders parentFolder = folderRepository.findByFolderPathAndArchivedIsFalse(parentFolderPath).orElseThrow(() -> {
                throw new IllegalArgumentException("Exception in create Folder: No such parent folder");
            });
            ArrayList<Folders> listSubFolder = new ArrayList<>();
            listSubFolder.add(folder);
            parentFolder.setSubFolders(listSubFolder);
            folderRepository.save(parentFolder);
        }
    }

    public String displayFile(CommandLineRequest body) {
        String filePath = commandLineHelper.getFileOrFolderPath(body.getCommandLine());
        Files file = fileRepository.findByFilePathAndArchivedIsFalse(filePath).orElseThrow(
                () -> {
                    throw new IllegalArgumentException("cat error: file not found");
                }
        );
        return file.getContent();
    }

    public void removeFile(CommandLineRequest body) {
        String standardizedCommand = commandLineHelper.standardizeString(body.getCommandLine());

        int numberOfPath = standardizedCommand.split(" ").length;

        while (numberOfPath > 1) {
            String[] paths = standardizedCommand.split(" ");
            String filePath = paths[paths.length - 1];
            // if file path contains .txt => it is a file
            if (filesAndFoldersHelper.isAFilePath(filePath)) {
                Files file = fileRepository.findByFilePathAndArchivedIsFalse(filePath).orElseThrow(
                        () -> {
                            throw new IllegalArgumentException("Exception in remove: No such file");
                        });
                file.setArchived(true);
                fileRepository.save(file);
            } else {
                Folders folder = folderRepository.findByFolderPathAndArchivedIsFalse(filePath).orElseThrow(
                        () -> {
                            throw new IllegalArgumentException("Exception in remove: No such file");
                        });
                folder.setArchived(true);
                folderRepository.save(folder);
            }

            numberOfPath--;
        }
    }

    public void updateFileOrFolder(CommandLineRequest body) {
        String standardizedCommand = commandLineHelper.standardizeString(body.getCommandLine());
        String path = standardizedCommand.split(" ")[1];
        if (filesAndFoldersHelper.isAFilePath(path)) {
            String newName = standardizedCommand.split(" ")[2];
            Files file = fileRepository.findByFilePathAndArchivedIsFalse(path)
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("Error at update: File not found");
                    });
            file.setFileName(newName);
            String data = commandLineHelper.getDataOfFile(standardizedCommand);
            if (!data.isEmpty()) {
                file.setContent(data);
            }
            fileRepository.save(file);
        } else {
            String newName = standardizedCommand.split(" ")[2];
            Folders folder = folderRepository.findByFolderPathAndArchivedIsFalse(path)
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("Error at update: Folder not found");
                    });
            folder.setFolderName(newName);
            folderRepository.save(folder);

            //Update sub folder in parent folder
            String parentFolderPath = commandLineHelper.getFolderParentPath(path);
            Folders parentFolder = folderRepository.findByFolderPathAndArchivedIsFalse(parentFolderPath).orElseThrow(() -> {
                throw new IllegalArgumentException("Exception in create Folder: No such parent folder");
            });
            ArrayList<Folders> listSubFolder = new ArrayList<>();
            listSubFolder.add(folder);
            parentFolder.setSubFolders(listSubFolder);
            folderRepository.save(parentFolder);
        }

    }

    public void moveFileOrFolder(CommandLineRequest body) {
        try {
            String standardizedCommand = commandLineHelper.standardizeString(body.getCommandLine());
            String oldPath = standardizedCommand.split(" ")[1];
            String newPath = standardizedCommand.split(" ")[2];
            folderRepository.findByFolderPathAndArchivedIsFalse(newPath).orElseThrow(() -> {
                throw new IllegalArgumentException("Exception in move: New folder not existed");
            });

            if (newPath.contains(oldPath)) {
                throw new IllegalArgumentException("Exception in move: New folder is subfolder");
            }

            if (filesAndFoldersHelper.checkFileIsExisted(oldPath) && filesAndFoldersHelper.isAFilePath(oldPath)) {
                String fileName = commandLineHelper.getFileName(oldPath);
                Files file = fileRepository.findByFilePathAndArchivedIsFalse(oldPath).orElseThrow(
                        () -> {
                            throw new IllegalArgumentException("Moving error: File not found");
                        }
                );
                file.setParentFolderPath(newPath);
                file.setFilePath(newPath.concat("/").concat(fileName));
                fileRepository.save(file);
            } else if (filesAndFoldersHelper.checkFolderIsExisted(oldPath)) {
                String folderName = commandLineHelper.getFileName(oldPath);
                Folders folder = folderRepository.findByFolderPathAndArchivedIsFalse(oldPath)
                        .orElseThrow(
                                () -> {
                                    throw new IllegalArgumentException("Moving error: Folder not found");
                                }
                        );
                ;
                folder.setFolderPath(newPath.concat("/").concat(folderName));
                folder.setParentPath(newPath);
                folderRepository.save(folder);

                //update sub folder in parent folder
                String parentFolderPath = commandLineHelper.getFolderParentPath(newPath);
                Folders parentFolder = folderRepository.findByFolderPathAndArchivedIsFalse(parentFolderPath).orElseThrow(() -> {
                    throw new IllegalArgumentException("Exception in create Folder: No such parent folder");
                });
                ArrayList<Folders> listSubFolder = new ArrayList<>();
                listSubFolder.add(folder);
                parentFolder.setSubFolders(listSubFolder);
                folderRepository.save(parentFolder);

            } else {
                throw new IllegalArgumentException("Moving error: No such element");
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Moving error: Can not move file/folder");
        }
    }

    public String changeDirect(CommandLineRequest body) {
        String standardizedCommand = commandLineHelper.standardizeString(body.getCommandLine());
        String path = commandLineHelper.getFileOrFolderPath(standardizedCommand);
        folderRepository.findByFolderPathAndArchivedIsFalse(path)
                .orElseThrow(() -> {
                    throw new IllegalArgumentException("Error: Not found folder");
                });
        return path;
    }

    public ViewResponse search(CommandLineRequest body) {
        try {
            String standardizedCommand = commandLineHelper.standardizeString(body.getCommandLine());
            String name = standardizedCommand.split(" ")[1];
            boolean hadFolderPath = standardizedCommand.split(" ").length > 2;
            if (hadFolderPath) {
                String folderPath = standardizedCommand.split(" ")[2];

                // get list of files
                List<Files> listOfFiles = fileRepository
                        .findByNameAndFolder(name, folderPath)
                        .orElseThrow(() -> {
                            throw new IllegalArgumentException("Find error: no file");
                        });

                // convert original list to response
                ArrayList<FileResponse> fileResponses = new ArrayList<>();
                listOfFiles.forEach(file -> {
                    FileResponse fileResponse = new FileResponse(file);
                    fileResponses.add(fileResponse);
                });

                // get list of folders
                List<Folders> listOfFolders = folderRepository
                        .findByNameAndFolderParent(name, folderPath)
                        .orElseThrow(() -> {
                            throw new IllegalArgumentException("Find error: no folders");
                        });

                // convert original list to response
                ArrayList<FolderResponse> folderResponses = new ArrayList<>();
                listOfFolders.forEach(folder -> {
                    FolderResponse response = new FolderResponse(folder);
                    folderResponses.add(response);
                });

                //return view
                ViewResponse viewResponse = new ViewResponse();
                viewResponse.setFiles(fileResponses);
                viewResponse.setFolders(folderResponses);
                return viewResponse;
            } else {
                List<Files> listOfFiles = fileRepository
                        .findByName(name)
                        .orElseThrow(() -> {
                            throw new IllegalArgumentException("Find error: no folders");
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
                            throw new IllegalArgumentException("Find error: no folders");
                        });
                ;

                // convert original list to response
                ArrayList<FolderResponse> folderResponses = new ArrayList<>();
                if (listOfFolders.size() > 0)
                    for (Folders folder : listOfFolders) {
                        FolderResponse response = new FolderResponse(folder);
                        folderResponses.add(response);
                    }

                // return view
                ViewResponse viewResponse = new ViewResponse();
                viewResponse.setFiles(fileResponses);
                viewResponse.setFolders(folderResponses);
                return viewResponse;
            }
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    public ViewResponse displayFilesAndFolder(CommandLineRequest body) {
        try {
            String standardizedCommand = commandLineHelper.standardizeString(body.getCommandLine());
            boolean hadFolderPath = standardizedCommand.split(" ").length > 2;
            String path = "";
            if (hadFolderPath) {
                path = standardizedCommand.split(" ")[2]; // use [FOLDER_PATH]
            } else {
                path = body.getPath(); //use path from body request
            }

            // get list of files
            List<Files> listOfFiles = fileRepository
                    .findByFolderPath(path)
                    .orElseThrow(() -> {
                        throw new IllegalArgumentException("ls error: no folders");
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
                        throw new IllegalArgumentException("ls error: no folders");
                    });

            // convert original list to response
            ArrayList<FolderResponse> folderResponses = new ArrayList<>();
            if (listOfFolders.size() > 0)
                for (Folders folder : listOfFolders) {
                    FolderResponse response = new FolderResponse(folder);
                    folderResponses.add(response);
                }

            ViewResponse viewResponse = new ViewResponse();
            viewResponse.setFiles(fileResponses);
            viewResponse.setFolders(folderResponses);
            return viewResponse; // use fo response API

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private void createParentPath(CommandLineRequest body) {
        // Create folder if command line has [-p] parameter and is not exist yet
        // if the command is cr [-p] PATH [DATA], get [-p] as a parentPathForCreate
        String parentPathForCreate = commandLineHelper.getParentPathForCreate(body.getCommandLine());

        //get parent path from parentPathForCreate
        String parentFolderPath = commandLineHelper.getFolderParentPath(parentPathForCreate);

        //if parentPathForCreate is not existed, create it
        if (folderRepository.findByFolderPathAndArchivedIsFalse(parentPathForCreate).isEmpty()) {
            Folders folder = new Folders();
            LocalDateTime createAt = LocalDateTime.now();
            folder.setCreateAt(createAt);
            folder.setFolderPath(parentPathForCreate);
            folder.setParentPath(parentFolderPath);
            folderRepository.save(folder);
        }
    }
}
