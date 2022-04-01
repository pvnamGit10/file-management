import { FileReponse } from "../components/models/Files";
import { FolderResponse } from "../components/models/Folder";

export const renderFiles = (file: FileReponse): string => {
  return file.fileName!!.concat(' | ')
          .concat(file.filePath!!).concat(' | ')
          .concat(file.size!!).concat(' | ')
          .concat(file.createAt!!.toLocaleString());
}

export const renderFolders = (folder: FolderResponse): string => {
  return folder.folderName!!.concat(' | ')
          .concat(folder.folderPath!!).concat(' | ')
          .concat(folder.parentPath!!).concat(' | ')
          .concat(folder.size!!).concat(' | ')
          .concat(folder.createAt!!.toLocaleString());
}